-- Criação do usuário da aplicação
ALTER SESSION SET CONTAINER=XEPDB1;

-- Criação do usuário
CREATE USER verdicomply IDENTIFIED BY verdicomply;

-- Concedendo permissões
GRANT CREATE SESSION TO verdicomply;
GRANT CREATE TABLE TO verdicomply;
GRANT CREATE SEQUENCE TO verdicomply;
GRANT CREATE PROCEDURE TO verdicomply;
GRANT CREATE TRIGGER TO verdicomply;
GRANT CREATE VIEW TO verdicomply;
GRANT UNLIMITED TABLESPACE TO verdicomply;
GRANT CREATE SYNONYM TO verdicomply;

-- Informando que o script concluiu com sucesso
EXIT;
