-- Inserindo dados na tabela norma_ambiental
INSERT INTO norma_ambiental (codigo_norma, descricao, orgao_fiscalizador)
VALUES ('N001', 'Emissão de CO2 controlada', 'IBAMA');

INSERT INTO norma_ambiental (codigo_norma, descricao, orgao_fiscalizador)
VALUES ('N002', 'Descarte correto de resíduos químicos', 'CETESB');

INSERT INTO norma_ambiental (codigo_norma, descricao, orgao_fiscalizador)
VALUES ('N003', 'Uso racional de água', 'ANA');

INSERT INTO norma_ambiental (codigo_norma, descricao, orgao_fiscalizador)
VALUES ('N004', 'Tratamento de efluentes', 'IBAMA');

INSERT INTO norma_ambiental (codigo_norma, descricao, orgao_fiscalizador)
VALUES ('N005', 'Controle de poluição sonora', 'CONAMA');

-- Inserindo dados na tabela departamento
INSERT INTO departamento (nome_departamento)
VALUES ('Manufatura');

INSERT INTO departamento (nome_departamento)
VALUES ('Logística');

INSERT INTO departamento (nome_departamento)
VALUES ('TI Sustentável');

INSERT INTO departamento (nome_departamento)
VALUES ('Infraestrutura');

INSERT INTO departamento (nome_departamento)
VALUES ('Recursos Naturais');

-- Inserindo dados na tabela auditoria
INSERT INTO auditoria (id_departamento, data_auditoria, auditor_responsavel, status_auditoria)
VALUES (1, TO_TIMESTAMP('01/10/2024', 'DD/MM/YYYY'), 'Carlos Silva', 'CONCLUÍDA');

INSERT INTO auditoria (id_departamento, data_auditoria, auditor_responsavel, status_auditoria)
VALUES (2, TO_TIMESTAMP('03/10/2024', 'DD/MM/YYYY'), 'Renata Lima', 'CONCLUÍDA');

INSERT INTO auditoria (id_departamento, data_auditoria, auditor_responsavel, status_auditoria)
VALUES (3, TO_TIMESTAMP('05/10/2024', 'DD/MM/YYYY'), 'André Souza', 'PENDENTE');

INSERT INTO auditoria (id_departamento, data_auditoria, auditor_responsavel, status_auditoria)
VALUES (4, TO_TIMESTAMP('07/10/2024', 'DD/MM/YYYY'), 'Mariana Costa', 'CONCLUÍDA');

INSERT INTO auditoria (id_departamento, data_auditoria, auditor_responsavel, status_auditoria)
VALUES (5, TO_TIMESTAMP('09/10/2024', 'DD/MM/YYYY'), 'Juliana Lopes', 'CONCLUÍDA');

-- Inserindo dados na tabela conformidade
INSERT INTO conformidade (id_auditoria, id_norma, esta_conforme, observacao)
VALUES (1, 1, 'S', 'Todos os filtros estão dentro do padrão');

INSERT INTO conformidade (id_auditoria, id_norma, esta_conforme, observacao)
VALUES (1, 2, 'N', 'Resíduos não separados corretamente');

INSERT INTO conformidade (id_auditoria, id_norma, esta_conforme, observacao)
VALUES (2, 4, 'S', 'Tratamento adequado implementado');

INSERT INTO conformidade (id_auditoria, id_norma, esta_conforme, observacao)
VALUES (1, 3, 'S', 'Sistema de reuso implementado');

INSERT INTO conformidade (id_auditoria, id_norma, esta_conforme, observacao)
VALUES (4, 5, 'N', 'Níveis de ruído acima do permitido');

INSERT INTO conformidade (id_auditoria, id_norma, esta_conforme, observacao)
VALUES (5, 1, 'N', 'Falta de relatório mensal sobre emissões');

-- Inserindo dados na tabela pendencia
INSERT INTO pendencia (id_conformidade, descricao_pendencia, prazo_resolucao, resolvida)
VALUES (2, 'Instalar separadores de resíduos químicos', TO_TIMESTAMP('15/10/2024', 'DD/MM/YYYY'), 'N');

INSERT INTO pendencia (id_conformidade, descricao_pendencia, prazo_resolucao, resolvida)
VALUES (5, 'Instalar isolamento acústico', TO_TIMESTAMP('20/10/2024', 'DD/MM/YYYY'), 'N');

INSERT INTO pendencia (id_conformidade, descricao_pendencia, prazo_resolucao, resolvida)
VALUES (3, 'Substituir filtros na chaminé principal', TO_TIMESTAMP('10/10/2024', 'DD/MM/YYYY'), 'N');
