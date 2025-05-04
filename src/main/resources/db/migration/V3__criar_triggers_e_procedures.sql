-- Trigger para geração automática de pendência ao registrar uma não conformidade
CREATE OR REPLACE TRIGGER TRG_GERA_PENDENCIA
AFTER INSERT ON CONFORMIDADE
FOR EACH ROW
WHEN (NEW.ESTA_CONFORME = 'N')
BEGIN
  INSERT INTO PENDENCIA (
    ID_CONFORMIDADE,
    DESCRICAO_PENDENCIA,
    PRAZO_RESOLUCAO,
    RESOLVIDA
  )
  VALUES (
    :NEW.ID_CONFORMIDADE,
    'Pendência automática gerada a partir de não conformidade',
    SYSDATE + 15,
    'N'
  );
END;

-- Procedimento para atualização automática do status da auditoria para "CONCLUÍDA"
CREATE OR REPLACE PROCEDURE ATUALIZAR_STATUS_AUDITORIA(p_id_auditoria IN NUMBER) AS
  v_total_conformidades NUMBER;
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

-- Procedimento para identificação de pendências vencidas e não resolvidas
CREATE OR REPLACE PROCEDURE VERIFICAR_PENDENCIAS_VENCIDAS AS
BEGIN
  FOR r IN (
    SELECT ID_PENDENCIA, DESCRICAO_PENDENCIA, PRAZO_RESOLUCAO
    FROM PENDENCIA
    WHERE RESOLVIDA = 'N' AND PRAZO_RESOLUCAO < SYSDATE
  ) LOOP
    DBMS_OUTPUT.PUT_LINE(
      '⚠ Pendência atrasada: ID ' || r.ID_PENDENCIA || ' - ' || r.DESCRICAO_PENDENCIA
    );
  END LOOP;
END;

-- Trigger para registro automático de log de conformidade
CREATE OR REPLACE TRIGGER TRG_LOG_CONFORMIDADE
AFTER INSERT OR UPDATE ON CONFORMIDADE
FOR EACH ROW
BEGIN
  INSERT INTO LOG_CONFORMIDADE (ID_CONFORMIDADE, ACAO, DETALHES)
  VALUES (
    NVL(:NEW.ID_CONFORMIDADE, :OLD.ID_CONFORMIDADE),
    CASE
      WHEN INSERTING THEN 'INSERÇÃO'
      ELSE 'ALTERAÇÃO'
    END,
    'Conformidade: ' || :NEW.ESTA_CONFORME || ', Obs: ' || :NEW.OBSERVACAO
  );
END;
