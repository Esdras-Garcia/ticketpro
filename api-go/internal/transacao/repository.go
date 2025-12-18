package transacao

import "database/sql"

type Repository struct {
	DB *sql.DB
}

func NewRepository(db *sql.DB) *Repository {
	return &Repository{DB: db}
}

func (r *Repository) Registrar(usuarioID int, valor float64, tipo, descricao string) error {
	sql := `INSERT INTO transacoes_carteira (usuario_id, valor, tipo, descricao) VALUES ($1, $2, $3, $4)`
	_, err := r.DB.Exec(sql, usuarioID, valor, tipo, descricao)
	return err
}
