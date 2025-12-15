package db

import (
	"database/sql"
	"fmt"
	"log"
	"os"
	"time"

	_ "github.com/lib/pq"
)

func Conectar() (*sql.DB, error) {
	connStr := fmt.Sprintf("host=%s user=%s password=%s dbname=%s sslmode=disable",
		os.Getenv("DB_HOST"), os.Getenv("DB_USER"), os.Getenv("DB_PASS"), os.Getenv("DB_NAME"))

	db, err := sql.Open("postgres", connStr)
	if err != nil {
		return nil, err
	}

	db.SetMaxOpenConns(25)
	db.SetMaxIdleConns(25)
	db.SetConnMaxLifetime(5 * time.Minute)

	counts := 0
	for {
		err = db.Ping()
		if err == nil {
			log.Println("Conexão com o banco estabelecida com sucesso!")
			break
		}

		log.Printf("Postgres ainda não está pronto... Erro: %s", err.Error())
		counts++
		log.Println("Aguardando 2 segundos antes de tentar novamente...")
		time.Sleep(2 * time.Second)

		if counts > 15 {
			return nil, fmt.Errorf("desistindo após várias tentativas: %v", err)
		}
	}

	return db, nil
}
