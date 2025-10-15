-- Criação da tabela NormaAmbiental com SERIAL
CREATE TABLE norma_ambiental (
    id_norma SERIAL PRIMARY KEY,
    codigo_norma VARCHAR(20) NOT NULL,
    titulo VARCHAR(100),
    descricao VARCHAR(200),
    orgao_fiscalizador VARCHAR(100),
    severidade VARCHAR(20)
);

-- Criação da tabela Departamento com SERIAL
CREATE TABLE departamento (
    id_departamento SERIAL PRIMARY KEY,
    nome_departamento VARCHAR(100) NOT NULL
);

-- Criação da tabela Auditoria com SERIAL
CREATE TABLE auditoria (
    id_auditoria SERIAL PRIMARY KEY,
    id_departamento INTEGER NOT NULL,
    data_auditoria TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    auditor_responsavel VARCHAR(100),
    status_auditoria VARCHAR(20),
    CONSTRAINT fk_auditoria_departamento FOREIGN KEY (id_departamento) REFERENCES departamento(id_departamento)
);

-- Criação da tabela Conformidade com SERIAL
CREATE TABLE conformidade (
    id_conformidade SERIAL PRIMARY KEY,
    id_auditoria INTEGER NOT NULL,
    id_norma INTEGER NOT NULL,
    esta_conforme CHAR(1) CHECK (esta_conforme IN ('S', 'N')),
    observacao VARCHAR(200),
    CONSTRAINT fk_conformidade_auditoria FOREIGN KEY (id_auditoria) REFERENCES auditoria(id_auditoria),
    CONSTRAINT fk_conformidade_norma FOREIGN KEY (id_norma) REFERENCES norma_ambiental(id_norma)
);

-- Criação da tabela Pendencia com SERIAL
CREATE TABLE pendencia (
    id_pendencia SERIAL PRIMARY KEY,
    id_conformidade INTEGER NOT NULL,
    descricao_pendencia VARCHAR(200),
    prazo_resolucao TIMESTAMP,
    resolvida CHAR(1) DEFAULT 'N' CHECK (resolvida IN ('S', 'N')),
    CONSTRAINT fk_pendencia_conformidade FOREIGN KEY (id_conformidade) REFERENCES conformidade(id_conformidade)
);

-- Criação da tabela LogConformidade com SERIAL
CREATE TABLE log_conformidade (
    id_log SERIAL PRIMARY KEY,
    id_conformidade INTEGER,
    acao VARCHAR(20),
    data_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    detalhes VARCHAR(200)
);
