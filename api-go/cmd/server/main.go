package main

import (
	"api-go/internal/db"
	"api-go/internal/ingresso"
	"api-go/internal/pedido"
	"api-go/internal/transacao"
	"api-go/internal/usuario"
	"log"
	"sync"
)

func main() {
	conexao, err := db.Conectar()
	if err != nil {
		log.Fatal(err)
	}
	defer conexao.Close()

	repoUsuario := usuario.NewRepository(conexao)
	repoIngresso := ingresso.NewRepository(conexao)
	repoTransacao := transacao.NewRepository(conexao)
	repoPedido := pedido.NewRepository(conexao)

	processador := &pedido.Processador{
		RepoPedido:    repoPedido,
		RepoUsuario:   repoUsuario,
		RepoIngresso:  repoIngresso,
		RepoTransacao: repoTransacao,
	}

	log.Println("Servidor de Processamento (Worker) Iniciado")

	var wg sync.WaitGroup
	numWorkers := 5

	for i := 1; i <= numWorkers; i++ {
		wg.Add(1)
		go func(id int) {
			defer wg.Done()
			processador.Iniciar(id)
		}(i)
	}

	wg.Wait()
}
