INSERT INTO usuarios (nome, email) VALUES 
('Ana', 'ana@email.com'),
('Bruno', 'bruno@email.com'),
('Carlos', 'carlos@email.com');

INSERT INTO eventos (nome, data_evento, localizacao, ativo) VALUES 
('Super Tech Conference 2025', '2025-11-20 09:00:00', 'Expo Center Norte', TRUE);

INSERT INTO ingressos (evento_id, codigo_assento, preco, disponivel)
SELECT 
    1,
    'A' || generate_series,
    500.00,
    TRUE
FROM generate_series(1, 10);

INSERT INTO ingressos (evento_id, codigo_assento, preco, disponivel)
SELECT 
    1, 
    'PISTA-' || generate_series, 
    150.00,
    TRUE
FROM generate_series(1, 100);

INSERT INTO pedidos_fila (usuario_id, evento_id, quantidade, status, data_pedido)
VALUES (2, 1, 1, 'PENDENTE', NOW());