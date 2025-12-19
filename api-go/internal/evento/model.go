package evento

import "time"

type Evento struct {
	ID                    int       `json:"id"`
	Nome                  string    `json:"nome"`
	DataEvento            time.Time `json:"dataEvento"`
	Localizacao           string    `json:"localizacao"`
	NumeroMaximoIngressos int       `json:"numeroMaximoIngressos"`
	Preco                 float64   `json:"preco"`
	Ativo                 bool      `json:"ativo"`
	IngressosDisponiveis  int       `json:"ingressosDisponiveis"`
}
