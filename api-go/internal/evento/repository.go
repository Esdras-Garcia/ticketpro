package evento

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

func (r *Repository) ListarAtivos() ([]Evento, error) {
	query := `
		SELECT 
			e.id, 
			e.nome, 
			e.data_evento, 
			e.localizacao, 
			e.numero_maximo_ingressos, 
			e.preco, 
			e.ativo,
			(SELECT COUNT(*) FROM ingressos i WHERE i.evento_id = e.id AND i.disponivel = TRUE) as disponiveis
		FROM eventos e 
		WHERE e.ativo = TRUE 
		ORDER BY e.data_evento ASC
	`

	rows, err := r.DB.Query(query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var eventos []Evento
	for rows.Next() {
		var e Evento
		if err := rows.Scan(
			&e.ID,
			&e.Nome,
			&e.DataEvento,
			&e.Localizacao,
			&e.NumeroMaximoIngressos,
			&e.Preco,
			&e.Ativo,
			&e.IngressosDisponiveis,
		); err != nil {
			log.Println("Erro ao escanear evento:", err)
			continue
		}
		eventos = append(eventos, e)
	}
	return eventos, nil
}
