package pedido

import "time"

type Pedido struct {
	ID         int64
	UsuarioID  int
	EventoID   int
	Quantidade int
	ValorTotal float64
	Status     string
	DataPedido time.Time
}
