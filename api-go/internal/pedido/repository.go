package pedido

import (
	"database/sql"
	"log"
)

type Repository struct {
	DB *sql.DB
}

func NewRepository(db *sql.DB) *Repository {
	return &Repository{DB: db}
}

func (r *Repository) BuscarPendentes(limite int) ([]Pedido, error) {
	query := `
		UPDATE pedidos_fila
		SET status = 'PROCESSANDO', data_atualizacao = NOW()
		WHERE id IN (
			SELECT id
			FROM pedidos_fila
			WHERE status = 'PENDENTE'
			ORDER BY data_pedido ASC
			FOR UPDATE SKIP LOCKED
			LIMIT $1
		)
		RETURNING id, usuario_id, evento_id, quantidade, valor_total, status`

	rows, err := r.DB.Query(query, limite)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var pedidos []Pedido
	for rows.Next() {
		var p Pedido
		if err := rows.Scan(&p.ID, &p.UsuarioID, &p.EventoID, &p.Quantidade, &p.ValorTotal, &p.Status); err != nil {
			log.Println("Erro ao escanear pedido:", err)
			continue
		}
		pedidos = append(pedidos, p)
	}
	return pedidos, nil
}

func (r *Repository) AtualizarStatus(id int64, status, erroMsg string) error {
	_, err := r.DB.Exec("UPDATE pedidos_fila SET status = $1, mensagem_erro = $2, data_atualizacao = NOW() WHERE id = $3", status, erroMsg, id)
	return err
}
