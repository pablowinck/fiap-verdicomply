-- Script para limpar o esquema do Oracle FIAP
-- Este script remove todas as tabelas e sequências do usuário

-- Desabilitar restrições de integridade referencial para possibilitar drop em qualquer ordem
BEGIN
    FOR c IN (SELECT table_name FROM user_tables) LOOP
        BEGIN
            EXECUTE IMMEDIATE 'ALTER TABLE ' || c.table_name || ' DISABLE CONSTRAINT ALL';
        EXCEPTION
            WHEN OTHERS THEN NULL;
        END;
    END LOOP;
END;
/

-- Remover tabelas
BEGIN
    FOR c IN (SELECT table_name FROM user_tables) LOOP
        BEGIN
            EXECUTE IMMEDIATE 'DROP TABLE ' || c.table_name || ' CASCADE CONSTRAINTS';
            DBMS_OUTPUT.PUT_LINE('Tabela ' || c.table_name || ' removida com sucesso.');
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('Erro ao remover tabela ' || c.table_name || ': ' || SQLERRM);
        END;
    END LOOP;
END;
/

-- Remover sequências
BEGIN
    FOR c IN (SELECT sequence_name FROM user_sequences) LOOP
        BEGIN
            EXECUTE IMMEDIATE 'DROP SEQUENCE ' || c.sequence_name;
            DBMS_OUTPUT.PUT_LINE('Sequência ' || c.sequence_name || ' removida com sucesso.');
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('Erro ao remover sequência ' || c.sequence_name || ': ' || SQLERRM);
        END;
    END LOOP;
END;
/

-- Listar tabelas remanescentes (se houver)
SELECT table_name FROM user_tables;

-- Listar sequências remanescentes (se houver)
SELECT sequence_name FROM user_sequences;
