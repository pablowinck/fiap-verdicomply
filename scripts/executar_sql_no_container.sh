#!/bin/bash

echo "=== ATUALIZANDO SCHEMA DE NORMAS AMBIENTAIS ==="

# Executar comandos SQL diretamente no container
docker exec verdicomply-api /bin/sh -c "java -cp /app/app.jar org.h2.tools.RunScript -url 'jdbc:h2:mem:testdb' -user sa -script - << EOF
-- Script para adicionar novas colunas à tabela NORMA_AMBIENTAL
ALTER TABLE NORMA_AMBIENTAL ADD COLUMN IF NOT EXISTS TITULO VARCHAR(100);
ALTER TABLE NORMA_AMBIENTAL ADD COLUMN IF NOT EXISTS SEVERIDADE VARCHAR(50);

-- Atualizar registros existentes com valores padrão
UPDATE NORMA_AMBIENTAL SET TITULO = CODIGO_NORMA WHERE TITULO IS NULL;
UPDATE NORMA_AMBIENTAL SET SEVERIDADE = 'MEDIA' WHERE SEVERIDADE IS NULL;

-- Mostrar schema atual
SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'NORMA_AMBIENTAL';
EOF"

echo "Schema atualizado com sucesso!"
