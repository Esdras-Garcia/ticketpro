package main

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"

	_ "github.com/lib/pq"
)

type Usuario struct {
	ID    int    `json:"id"`
	Nome  string `json:"nome"`
	Email string `json:"email"`
}

func main() {
	dbHost := os.Getenv("DB_HOST")
	dbUser := os.Getenv("DB_USER")
	dbPass := os.Getenv("DB_PASS")
	dbName := os.Getenv("DB_NAME")

	connStr := fmt.Sprintf("host=%s user=%s password=%s dbname=%s sslmode=disable", dbHost, dbUser, dbPass, dbName)

	db, err := sql.Open("postgres", connStr)
	if err != nil {
		log.Fatal("Erro ao abrir conexão: ", err)
	}
	defer db.Close()

	if err = db.Ping(); err != nil {
		log.Fatal("Erro ao conectar no banco: ", err)
	}
	fmt.Println("Conectado ao banco com sucesso!")

	http.HandleFunc("/users", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Content-Type", "application/json")

		rows, err := db.Query("SELECT id, nome, email FROM usuarios")
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		defer rows.Close()

		var usuarios []Usuario
		for rows.Next() {
			var u Usuario
			if err := rows.Scan(&u.ID, &u.Nome, &u.Email); err != nil {
				log.Println(err)
				continue
			}
			usuarios = append(usuarios, u)
		}

		json.NewEncoder(w).Encode(usuarios)
	})

	fmt.Println("Servidor rodando na porta 8080...")
	log.Fatal(http.ListenAndServe(":8080", nil))
}
