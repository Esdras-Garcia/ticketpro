package worker

import (
	"api-go/internal/repository"
	"database/sql"
	"log"
	"time"
)

func Iniciar(repo *repository.PedidoRepository, idWorker int) {
	log.Printf("Worker #%d iniciado...", idWorker)

	for {
		err := repo.ProcessarProximoItem()

		if err != nil {
			if err == sql.ErrNoRows {
				time.Sleep(2 * time.Second)
			} else {
				log.Printf("Worker #%d erro: %v", idWorker, err)
				time.Sleep(1 * time.Second)
			}
		}
	}
}
