-- Trigger para geração automática de pendência
CREATE OR REPLACE TRIGGER after_conformidade_insert
AFTER INSERT ON CONFORMIDADE
FOR EACH ROW
BEGIN
    IF :NEW.ESTA_CONFORME = 'N' THEN
        INSERT INTO PENDENCIA (ID_CONFORMIDADE, DESCRICAO_PENDENCIA, PRAZO_RESOLUCAO, RESOLVIDA)
        VALUES (:NEW.ID_CONFORMIDADE, 'Resolver não conformidade: ' || COALESCE(:NEW.OBSERVACAO, 'Sem observação'),
                SYSDATE + 30, 'N');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER after_conformidade_update
AFTER UPDATE OF ESTA_CONFORME ON CONFORMIDADE
FOR EACH ROW
WHEN (OLD.ESTA_CONFORME = 'S' AND NEW.ESTA_CONFORME = 'N')
BEGIN
    INSERT INTO PENDENCIA (ID_CONFORMIDADE, DESCRICAO_PENDENCIA, PRAZO_RESOLUCAO, RESOLVIDA)
    VALUES (:NEW.ID_CONFORMIDADE, 'Resolver não conformidade: ' || COALESCE(:NEW.OBSERVACAO, 'Sem observação'),
            SYSDATE + 30, 'N');
END;
/

-- Procedimento para atualização automática do status da auditoria para "CONCLUÍDA"
CREATE OR REPLACE PROCEDURE atualizar_status_auditoria(p_id_auditoria INTEGER)
AS
DECLARE
  v_total_conformidades INTEGER;
BEGIN
  SELECT COUNT(*) INTO v_total_conformidades
  FROM CONFORMIDADE
  WHERE ID_AUDITORIA = p_id_auditoria;

  IF v_total_conformidades >= 3 THEN
    UPDATE AUDITORIA
    SET STATUS_AUDITORIA = 'CONCLUÍDA'
    WHERE ID_AUDITORIA = p_id_auditoria;
  END IF;
END;
/

-- Procedimento para identificação de pendências vencidas e não resolvidas
CREATE OR REPLACE PROCEDURE verificar_pendencias_em_atraso(
    p_pendencias_cursor OUT SYS_REFCURSOR
)
AS
BEGIN
    OPEN p_pendencias_cursor FOR
    SELECT 
        p.ID_PENDENCIA,
        p.ID_CONFORMIDADE,
        p.DESCRICAO_PENDENCIA,
        TRUNC(SYSDATE - p.PRAZO_RESOLUCAO) AS dias_em_atraso
    FROM 
        PENDENCIA p
    WHERE 
        p.RESOLVIDA = 'N' AND 
        p.PRAZO_RESOLUCAO < SYSDATE
    ORDER BY 
        dias_em_atraso DESC;
END;
/

-- Trigger para registro automático de log de conformidade (INSERT)
CREATE OR REPLACE TRIGGER trg_log_conformidade_insert
AFTER INSERT ON CONFORMIDADE
FOR EACH ROW
BEGIN
    INSERT INTO LOG_CONFORMIDADE (ID_CONFORMIDADE, ACAO, DETALHES)
    VALUES (:NEW.ID_CONFORMIDADE, 'INSERT', 'Nova conformidade registrada');
END;
/

-- Trigger para registro automático de log de conformidade (UPDATE)
CREATE OR REPLACE TRIGGER trg_log_conformidade_update
AFTER UPDATE ON CONFORMIDADE
FOR EACH ROW
BEGIN
    INSERT INTO LOG_CONFORMIDADE (ID_CONFORMIDADE, ACAO, DETALHES)
    VALUES (:NEW.ID_CONFORMIDADE, 'UPDATE', 'Conformidade atualizada');
END;
/

-- Trigger para registro automático de log de conformidade (DELETE)
CREATE OR REPLACE TRIGGER trg_log_conformidade_delete
AFTER DELETE ON CONFORMIDADE
FOR EACH ROW
BEGIN
    INSERT INTO LOG_CONFORMIDADE (ID_CONFORMIDADE, ACAO, DETALHES)
    VALUES (:OLD.ID_CONFORMIDADE, 'DELETE', 'Conformidade removida');
END;
/
