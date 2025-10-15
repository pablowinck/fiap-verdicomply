-- Criação da tabela de usuários com SERIAL
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Inserção de usuários iniciais
-- Senhas: admin123, gestor123, auditor123 (hash BCrypt gerado e verificado com strength 10)
-- Hash para "admin123": $2b$10$68YAAXLhhY8Pfdz45SOyUOKsFLIW1rJ2KxP8fzx1RAR7V4fFMwxw2
-- Hash para "gestor123": $2b$10$0ORLYAff.uTJfkwfIJVXBuS7sn/6SuUwab9RTATRbomBe9rIlERUC
-- Hash para "auditor123": $2b$10$vZJPZVis4Uf/lSTHzviZGOHGjBkfsTGt.GDuUMAepCtkzysG5C736
INSERT INTO usuarios (username, password, role)
VALUES ('admin', '$2b$10$68YAAXLhhY8Pfdz45SOyUOKsFLIW1rJ2KxP8fzx1RAR7V4fFMwxw2', 'ADMIN');

INSERT INTO usuarios (username, password, role)
VALUES ('gestor', '$2b$10$0ORLYAff.uTJfkwfIJVXBuS7sn/6SuUwab9RTATRbomBe9rIlERUC', 'GESTOR');

INSERT INTO usuarios (username, password, role)
VALUES ('auditor', '$2b$10$vZJPZVis4Uf/lSTHzviZGOHGjBkfsTGt.GDuUMAepCtkzysG5C736', 'AUDITOR');
