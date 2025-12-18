package usuario

import "database/sql"

type Repository struct {
	DB *sql.DB
}

func NewRepository(db *sql.DB) *Repository {
	return &Repository{DB: db}
}

func (r *Repository) BuscarSaldo(usuarioID int) (float64, error) {
	var saldo float64
	err := r.DB.QueryRow("SELECT saldo FROM usuarios WHERE id = $1", usuarioID).Scan(&saldo)
	return saldo, err
}

func (r *Repository) AtualizarSaldo(usuarioID int, novoSaldo float64) error {
	_, err := r.DB.Exec("UPDATE usuarios SET saldo = $1 WHERE id = $2", novoSaldo, usuarioID)
	return err
}
