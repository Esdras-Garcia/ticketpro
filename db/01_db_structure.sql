CREATE TABLE usuarios (
	id SERIAL PRIMARY KEY,
	nome VARCHAR(100) NOT NULL,
	email VARCHAR(150) UNIQUE NOT NULL,
	data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE eventos (
	id SERIAL PRIMARY KEY,
	nome VARCHAR(150) NOT NULL,
	data_evento TIMESTAMP NOT NULL,
	localizacao VARCHAR(100),
	ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE ingressos (
	id SERIAL PRIMARY KEY,
	evento_id INT REFERENCES eventos(id),
	codigo_assento VARCHAR(20),
	preco DECIMAL(10, 2) NOT NULL,
	disponivel BOOLEAN DEFAULT TRUE,
	usuario_id INT,
	pedido_id INT,
	versao INT DEFAULT 0
);

CREATE TABLE pedidos_fila (
	id BIGSERIAL PRIMARY KEY,
	usuario_id INT REFERENCES usuarios(id),
	evento_id INT REFERENCES eventos(id),
	quantidade INT DEFAULT 1,
	status VARCHAR(20) DEFAULT 'PENDENTE',
	mensagem_erro VARCHAR(255),
	data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	data_atualizacao TIMESTAMP
);

CREATE INDEX idx_pedidos_status ON pedidos_fila(status);
CREATE INDEX idx_ingressos_evento ON ingressos(evento_id) WHERE disponivel = TRUE;