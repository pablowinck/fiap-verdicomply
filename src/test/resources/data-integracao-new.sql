-- Dados para testes de integracao (H2)

-- Insercao de usuarios para testes
INSERT INTO usuarios (username, password, role)
VALUES
    ('admin', '$2a$10$GRHs0jwtT.lFrS5A5aiGYuH8NxC536Gjclf1.3g5icjriX4408.Be', 'ADMIN'), -- Senha: admin
    ('gestor', '$2a$10$/irWlpmP4bDrUJg69jFCoO4BJQbgwfR/xNjNNgYymiXRWaVcu9bIe', 'GESTOR'), -- Senha: gestor
    ('auditor', '$2a$10$r7EKTHkVxjRgG0qqqbmiXeXIlaGNJCI5or.T/HFfczzyQqdqDo6Qi', 'AUDITOR'); -- Senha: auditor

-- Insercao de departamentos para testes (com nomes unicos para evitar duplicacao)
INSERT INTO departamento (nome_departamento)
VALUES
    ('Manufatura_Integracao'),
    ('Logística_Integracao'),
    ('Operações_Integracao');
