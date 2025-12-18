CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    saldo DECIMAL(10, 2) DEFAULT 0.00,
    tipo_usuario VARCHAR(20) DEFAULT 'CLIENTE',
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
    usuario_id INT REFERENCES usuarios(id),
    pedido_id INT,
    versao INT DEFAULT 0
);

CREATE TABLE pedidos_fila (
    id BIGSERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuarios(id),
    evento_id INT REFERENCES eventos(id),
    quantidade INT DEFAULT 1,
    valor_total DECIMAL(10, 2),
    status VARCHAR(20) DEFAULT 'PENDENTE',
    mensagem_erro VARCHAR(255),
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP
);

CREATE TABLE transacoes_carteira (
    id BIGSERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuarios(id),
    valor DECIMAL(10, 2) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    descricao VARCHAR(255),
    data_transacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pedidos_status ON pedidos_fila(status);
CREATE INDEX idx_ingressos_evento ON ingressos(evento_id) WHERE disponivel = TRUE;
CREATE INDEX idx_transacoes_usuario ON transacoes_carteira(usuario_id);