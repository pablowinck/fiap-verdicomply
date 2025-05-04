-- Dados para testes de integração (H2)

-- Inserção de usuários para testes
INSERT INTO usuarios (username, password, role) 
VALUES 
    ('admin', '$2a$10$7hmMTgOkoQTPmzhj7CCOaeN1t5PV0K3/3e7WKTat1swQBOVeSqF0.', 'ADMIN'), -- Senha: admin
    ('gestor', '$2a$10$GjVBNmk.45fgzaMq.QqOEemt2OHzLEyBL7GFv2SK/4Zf2745MLdWu', 'GESTOR'), -- Senha: gestor
    ('auditor', '$2a$10$qNlIAqbcYMdLFci9AfxOZ.xcEk/3E60XoPZAi4SuJWQEQZT9OT9Nm', 'AUDITOR'); -- Senha: auditor

-- Inserção de departamentos para testes (com nomes únicos para evitar duplicação)
INSERT INTO departamento (nome_departamento) 
VALUES 
    ('Manufatura_Integracao'),
    ('Logística_Integracao'),
    ('Operações_Integracao');
