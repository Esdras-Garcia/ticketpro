package repository

import (
	"api-go/internal/model"
	"database/sql"
	"fmt"
)

type PedidoRepository struct {
	DB *sql.DB
}

func NewPedidoRepository(db *sql.DB) *PedidoRepository {
	return &PedidoRepository{DB: db}
}

func (r *PedidoRepository) ProcessarProximoItem() error {
	tx, err := r.DB.Begin()
	if err != nil {
		return err
	}
	defer tx.Rollback()

	var p model.PedidoFila
	row := tx.QueryRow(`
		SELECT id, usuario_id, evento_id, quantidade 
		FROM pedidos_fila 
		WHERE status = 'PENDENTE' 
		ORDER BY data_pedido ASC 
		LIMIT 1 
		FOR UPDATE SKIP LOCKED
	`)

	if err := row.Scan(&p.ID, &p.UsuarioID, &p.EventoID, &p.Quantidade); err != nil {
		return err
	}

	fmt.Printf("[Worker] Iniciando processamento do Pedido %d (Solicitado: %d)\n", p.ID, p.Quantidade)

	res, err := tx.Exec(`
		UPDATE ingressos 
		SET disponivel = false, pedido_id = $1, usuario_id = $2 
		WHERE id IN (
			SELECT id FROM ingressos 
			WHERE evento_id = $3 AND disponivel = true 
			ORDER BY id ASC 
			LIMIT $4 
			FOR UPDATE
		)
	`, p.ID, p.UsuarioID, p.EventoID, p.Quantidade)

	if err != nil {
		return err
	}

	rowsAffected, _ := res.RowsAffected()

	fmt.Printf("[Worker] > UPDATE INGRESSOS: %d ingressos marcados como indisponíveis para o Pedido %d\n", rowsAffected, p.ID)

	novoStatus := "SEM_ESTOQUE"

	if rowsAffected == int64(p.Quantidade) {
		novoStatus = "APROVADO"
	} else {
		if rowsAffected > 0 {
			fmt.Printf("[Worker] ! ALERTA: Pedido %d incompleto. Devolvendo %d ingressos...\n", p.ID, rowsAffected)
			_, err = tx.Exec(`
				UPDATE ingressos 
				SET disponivel = true, pedido_id = NULL, usuario_id = NULL 
				WHERE pedido_id = $1
			`, p.ID)

			if err != nil {
				return err
			}
		}
	}

	_, err = tx.Exec("UPDATE pedidos_fila SET status = $1, data_atualizacao = NOW() WHERE id = $2", novoStatus, p.ID)
	if err != nil {
		return err
	}

	if novoStatus == "APROVADO" {
		fmt.Printf("[Worker] >> SUCESSO: Pedido %d atualizado para APROVADO no banco.\n", p.ID)
	} else {
		fmt.Printf("[Worker] XX FALHA: Pedido %d atualizado para %s.\n", p.ID, novoStatus)
	}

	return tx.Commit()
}
