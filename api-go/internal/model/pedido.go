package model

import "time"

type PedidoFila struct {
	ID              int
	UsuarioID       int
	EventoID        int
	Quantidade      int
	Status          string
	DataPedido      time.Time
	DataAtualizacao time.Time
}
