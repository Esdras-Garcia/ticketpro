package ingresso

import "database/sql"

type Repository struct {
	DB *sql.DB
}

func NewRepository(db *sql.DB) *Repository {
	return &Repository{DB: db}
}

func (r *Repository) ReservarIngressos(eventoID, qtd int, pedidoID int64, usuarioID int) (bool, error) {

	var disponiveis int
	err := r.DB.QueryRow("SELECT COUNT(*) FROM ingressos WHERE evento_id = $1 AND disponivel = true", eventoID).Scan(&disponiveis)
	if err != nil {
		return false, err
	}
	if disponiveis < qtd {
		return false, nil
	}

	query := `
		UPDATE ingressos
		SET disponivel = FALSE, pedido_id = $1, usuario_id = $2
		WHERE id IN (
			SELECT id FROM ingressos
			WHERE evento_id = $3 AND disponivel = TRUE
			LIMIT $4
		)
	`

	res, err := r.DB.Exec(query, pedidoID, usuarioID, eventoID, qtd)
	if err != nil {
		return false, err
	}

	afetados, _ := res.RowsAffected()
	return afetados == int64(qtd), nil
}
