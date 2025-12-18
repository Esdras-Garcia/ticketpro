INSERT INTO usuarios (nome, email, senha, saldo, tipo_usuario) VALUES 
('Admin', 'admin@ticketpro.com', '123456', 0.00, 'ADMIN'),
('Ana Cliente', 'ana@email.com', '123456', 1000.00, 'CLIENTE'),
('Bruno Cliente', 'bruno@email.com', '123456', 50.00, 'CLIENTE');

INSERT INTO eventos (nome, data_evento, localizacao, ativo) VALUES 
('Super Tech Conference 2025', '2025-11-20 09:00:00', 'Expo Center Norte', TRUE);

INSERT INTO ingressos (evento_id, codigo_assento, preco, disponivel)
SELECT 1, 'VIP-' || generate_series, 500.00, TRUE
FROM generate_series(1, 10);

INSERT INTO ingressos (evento_id, codigo_assento, preco, disponivel)
SELECT 1, 'PISTA-' || generate_series, 150.00, TRUE
FROM generate_series(1, 50);