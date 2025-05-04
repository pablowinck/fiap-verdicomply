-- Dados para testes de integração com Cucumber (H2)

-- Inserção de usuários para testes
INSERT INTO usuarios (username, password, role) 
VALUES 
    ('admin', '$2a$10$0q7ggf7o.o9c7vpk8/IwL.dZR3/l0b5Bos8iYmJFqgJ4EJXCcO1vq', 'ADMIN'), -- Senha: admin
    ('gestor', '$2a$10$ux6T2s/kQl5nTDaZQ2vgHuDaLRyR8x2lQ4MXJMt0xAJGj.yl4I1f.', 'GESTOR'), -- Senha: gestor
    ('auditor', '$2a$10$lF.nM/O8SY/3D3xypBkfyO/0wAAA3pFUKrYVe19znRLMxMbBxP1Im', 'AUDITOR'); -- Senha: auditor

-- Inserção de departamentos para testes
INSERT INTO departamento (nome_departamento) 
VALUES 
    ('Manufatura'),
    ('Logística'),
    ('Operações');
