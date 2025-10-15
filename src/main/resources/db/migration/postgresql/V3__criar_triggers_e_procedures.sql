-- Função para gerar pendência automaticamente
CREATE OR REPLACE FUNCTION gerar_pendencia_automatica()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.esta_conforme = 'N' THEN
        INSERT INTO pendencia (id_conformidade, descricao_pendencia, prazo_resolucao, resolvida)
        VALUES (NEW.id_conformidade, 'Resolver não conformidade: ' || COALESCE(NEW.observacao, 'Sem observação'),
                CURRENT_TIMESTAMP + INTERVAL '30 days', 'N');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para geração automática de pendência após INSERT
CREATE TRIGGER after_conformidade_insert
AFTER INSERT ON conformidade
FOR EACH ROW
EXECUTE FUNCTION gerar_pendencia_automatica();

-- Função para gerar pendência em UPDATE
CREATE OR REPLACE FUNCTION gerar_pendencia_em_update()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.esta_conforme = 'S' AND NEW.esta_conforme = 'N' THEN
        INSERT INTO pendencia (id_conformidade, descricao_pendencia, prazo_resolucao, resolvida)
        VALUES (NEW.id_conformidade, 'Resolver não conformidade: ' || COALESCE(NEW.observacao, 'Sem observação'),
                CURRENT_TIMESTAMP + INTERVAL '30 days', 'N');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para geração automática de pendência após UPDATE
CREATE TRIGGER after_conformidade_update
AFTER UPDATE OF esta_conforme ON conformidade
FOR EACH ROW
EXECUTE FUNCTION gerar_pendencia_em_update();

-- Procedimento para atualização automática do status da auditoria para "CONCLUÍDA"
CREATE OR REPLACE FUNCTION atualizar_status_auditoria(p_id_auditoria INTEGER)
RETURNS VOID AS $$
DECLARE
  v_total_conformidades INTEGER;
BEGIN
  SELECT COUNT(*) INTO v_total_conformidades
  FROM conformidade
  WHERE id_auditoria = p_id_auditoria;

  IF v_total_conformidades >= 3 THEN
    UPDATE auditoria
    SET status_auditoria = 'CONCLUÍDA'
    WHERE id_auditoria = p_id_auditoria;
  END IF;
END;
$$ LANGUAGE plpgsql;

-- Função para verificar pendências em atraso
CREATE OR REPLACE FUNCTION verificar_pendencias_em_atraso()
RETURNS TABLE (
    id_pendencia INTEGER,
    id_conformidade INTEGER,
    descricao_pendencia VARCHAR(200),
    dias_em_atraso INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        p.id_pendencia,
        p.id_conformidade,
        p.descricao_pendencia,
        CAST(EXTRACT(DAY FROM CURRENT_TIMESTAMP - p.prazo_resolucao) AS INTEGER) AS dias_em_atraso
    FROM
        pendencia p
    WHERE
        p.resolvida = 'N' AND
        p.prazo_resolucao < CURRENT_TIMESTAMP
    ORDER BY
        dias_em_atraso DESC;
END;
$$ LANGUAGE plpgsql;

-- Função para registro de log de conformidade
CREATE OR REPLACE FUNCTION registrar_log_conformidade()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO log_conformidade (id_conformidade, acao, detalhes)
        VALUES (NEW.id_conformidade, 'INSERT', 'Nova conformidade registrada');
        RETURN NEW;
    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO log_conformidade (id_conformidade, acao, detalhes)
        VALUES (NEW.id_conformidade, 'UPDATE', 'Conformidade atualizada');
        RETURN NEW;
    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO log_conformidade (id_conformidade, acao, detalhes)
        VALUES (OLD.id_conformidade, 'DELETE', 'Conformidade removida');
        RETURN OLD;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Trigger para registro automático de log de conformidade (INSERT)
CREATE TRIGGER trg_log_conformidade_insert
AFTER INSERT ON conformidade
FOR EACH ROW
EXECUTE FUNCTION registrar_log_conformidade();

-- Trigger para registro automático de log de conformidade (UPDATE)
CREATE TRIGGER trg_log_conformidade_update
AFTER UPDATE ON conformidade
FOR EACH ROW
EXECUTE FUNCTION registrar_log_conformidade();

-- Trigger para registro automático de log de conformidade (DELETE)
CREATE TRIGGER trg_log_conformidade_delete
AFTER DELETE ON conformidade
FOR EACH ROW
EXECUTE FUNCTION registrar_log_conformidade();
