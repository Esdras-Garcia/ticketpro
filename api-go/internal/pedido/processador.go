package pedido

import (
	"api-go/internal/ingresso"
	"api-go/internal/transacao"
	"api-go/internal/usuario"
	"log"
	"time"
)

type Processador struct {
	RepoPedido    *Repository
	RepoUsuario   *usuario.Repository
	RepoIngresso  *ingresso.Repository
	RepoTransacao *transacao.Repository
}

// Loop infinito para processar a fila
func (p *Processador) Iniciar(workerID int) {

	log.Printf("Worker #%d iniciado", workerID)

	for {
		pedidos, err := p.RepoPedido.BuscarPendentes(1)
		if err != nil {
			log.Println("Erro ao buscar fila:", err)
			time.Sleep(5 * time.Second)
			continue
		}

		if len(pedidos) == 0 {
			time.Sleep(2 * time.Second)
			continue
		}

		pedido := pedidos[0]
		p.processarPedido(pedido)
	}
}

func (p *Processador) processarPedido(pedido Pedido) {

	log.Printf("Processando pedido %d (User: %d, Valor: %.2f)", pedido.ID, pedido.UsuarioID, pedido.ValorTotal)

	saldo, err := p.RepoUsuario.BuscarSaldo(pedido.UsuarioID)
	if err != nil {
		p.RepoPedido.AtualizarStatus(pedido.ID, "ERRO_SISTEMA", "Erro ao buscar saldo")
		return
	}

	if saldo < pedido.ValorTotal {
		p.RepoPedido.AtualizarStatus(pedido.ID, "REJEITADO", "Saldo insuficiente")
		log.Printf("Pedido %d rejeitado: Saldo insuficiente", pedido.ID)
		return
	}

	reservou, err := p.RepoIngresso.ReservarIngressos(pedido.EventoID, pedido.Quantidade, pedido.ID, pedido.UsuarioID)
	if err != nil {
		p.RepoPedido.AtualizarStatus(pedido.ID, "ERRO_SISTEMA", "Erro ao reservar ingressos")
		return
	}
	if !reservou {
		p.RepoPedido.AtualizarStatus(pedido.ID, "SEM_ESTOQUE", "Não há ingressos disponíveis")
		log.Printf("Pedido %d rejeitado: Sem estoque", pedido.ID)
		return
	}

	novoSaldo := saldo - pedido.ValorTotal
	err = p.RepoUsuario.AtualizarSaldo(pedido.UsuarioID, novoSaldo)
	if err != nil {
		p.RepoPedido.AtualizarStatus(pedido.ID, "ERRO_CRITICO", "Falha ao debitar saldo")
		return
	}

	p.RepoTransacao.Registrar(pedido.UsuarioID, -pedido.ValorTotal, "COMPRA", "Compra de Ingressos TicketPro")

	p.RepoPedido.AtualizarStatus(pedido.ID, "APROVADO", "")
	log.Printf("Pedido %d APROVADO com sucesso!", pedido.ID)
}
