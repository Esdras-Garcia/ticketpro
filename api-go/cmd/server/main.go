package main

import (
	"api-go/internal/db"
	"api-go/internal/repository"
	"api-go/internal/worker"
	"log"
	"net/http"
)

func main() {
	conexao, err := db.Conectar()
	if err != nil {
		log.Fatal(err)
	}
	defer conexao.Close()

	pedidoRepo := repository.NewPedidoRepository(conexao)

	qtdWorkers := 5
	for i := 1; i <= qtdWorkers; i++ {
		go worker.Iniciar(pedidoRepo, i)
	}

	http.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
		w.Write([]byte("Sistema operando e workers ativos"))
	})

	log.Printf("Servidor e Workers rodando na porta 8080...")
	log.Fatal(http.ListenAndServe(":8080", nil))
}
