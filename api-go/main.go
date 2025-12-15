package main

import (
	"fmt"
	"net/http"
)

func main() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, "API Golang rodando! Conectada a TicketPro.")
	})

	fmt.Println("Server ouvindo na porta 8080...")
	http.ListenAndServe(":8080", nil)
}
