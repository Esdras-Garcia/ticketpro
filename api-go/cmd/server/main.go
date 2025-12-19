package main

import (
	"api-go/internal/db"
	"api-go/internal/evento"
	"api-go/internal/ingresso"
	"api-go/internal/pedido"
	"api-go/internal/transacao"
	"api-go/internal/usuario"
	"encoding/json"
	"log"
	"net/http"
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
	repoEvento := evento.NewRepository(conexao)

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

	http.HandleFunc("/eventos", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Content-Type", "application/json")

		if r.Method != http.MethodGet {
			http.Error(w, "Método não permitido", http.StatusMethodNotAllowed)
			return
		}

		lista, err := repoEvento.ListarAtivos()
		if err != nil {
			http.Error(w, "Erro ao listar eventos", http.StatusInternalServerError)
			log.Println("Erro HTTP Eventos:", err)
			return
		}

		json.NewEncoder(w).Encode(lista)
	})

	go func() {
		if err := http.ListenAndServe(":8080", nil); err != nil {
			log.Fatal(err)
		}
	}()

	wg.Wait()
}
