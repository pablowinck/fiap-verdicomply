#!/bin/bash

# Script para preparar o ambiente de teste da API VerdiComply
# Autor: Pablo Winter
# Data: 05/05/2025

# Cores para formatação
VERDE='\033[0;32m'
VERMELHO='\033[0;31m'
AMARELO='\033[0;33m'
RESET='\033[0m'

# URL base da API
BASE_URL="http://localhost:8080"

echo -e "${AMARELO}=== PREPARANDO AMBIENTE PARA TESTES POSTMAN ===${RESET}"

# 1. Verificar se a API está rodando
echo -e "\n${AMARELO}Verificando se a API está rodando...${RESET}"
HEALTH_CHECK=$(curl -s -o /dev/null -w "%{http_code}" ${BASE_URL}/api/public/health)

if [ "$HEALTH_CHECK" != "200" ]; then
    echo -e "${VERMELHO}Erro: A API não está rodando. Inicie o servidor antes de executar este script.${RESET}"
    exit 1
fi

echo -e "${VERDE}✓ API rodando corretamente.${RESET}"

# 2. Autenticar como admin
echo -e "\n${AMARELO}Autenticando como admin...${RESET}"
LOGIN_RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123"}' \
    ${BASE_URL}/api/public/auth/login)

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | sed 's/"token":"//')

if [ -z "$TOKEN" ]; then
    echo -e "${VERMELHO}Erro: Não foi possível obter token de autenticação.${RESET}"
    exit 1
fi

echo -e "${VERDE}✓ Autenticação realizada com sucesso.${RESET}"

# 3. Criar dados básicos para teste
echo -e "\n${AMARELO}Criando dados básicos para teste...${RESET}"

# 3.1 Criar uma norma ambiental
echo -e "\n${AMARELO}Criando norma ambiental de teste...${RESET}"
NORMA_RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d '{
        "codigoNorma": "TEST-001",
        "descricao": "Norma de Teste Postman",
        "dataPublicacao": "2025-01-01",
        "orgaoFiscalizador": "IBAMA",
        "titulo": "Norma para Testes Automatizados",
        "severidade": "ALTA"
    }' \
    ${BASE_URL}/api/normas)

NORMA_ID=$(echo $NORMA_RESPONSE | grep -o '"id":[0-9]*' | head -1 | sed 's/"id"://')

if [ -z "$NORMA_ID" ]; then
    echo -e "${VERMELHO}Aviso: Não foi possível criar norma ambiental. Verificando se já existe...${RESET}"
    NORMAS_RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" ${BASE_URL}/api/normas)
    NORMA_ID=$(echo $NORMAS_RESPONSE | grep -o '"id":[0-9]*' | head -1 | sed 's/"id"://')
    
    if [ -z "$NORMA_ID" ]; then
        echo -e "${VERMELHO}Erro: Não foi possível obter ID de norma ambiental.${RESET}"
        NORMA_ID=1
    else
        echo -e "${VERDE}✓ Utilizando norma existente com ID: $NORMA_ID${RESET}"
    fi
else
    echo -e "${VERDE}✓ Norma ambiental criada com ID: $NORMA_ID${RESET}"
fi

# 3.2 Criar uma auditoria
echo -e "\n${AMARELO}Criando auditoria de teste...${RESET}"
DATA_ATUAL=$(date +"%Y-%m-%d")
AUDITORIA_RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{
        \"descricao\": \"Auditoria de Teste Postman\",
        \"status\": \"PROGRAMADA\",
        \"dataAuditoria\": \"$DATA_ATUAL\",
        \"departamentoId\": 1,
        \"observacoes\": \"Criada para testes da API\",
        \"auditorResponsavel\": \"Teste Postman\"
    }" \
    ${BASE_URL}/api/auditorias)

AUDITORIA_ID=$(echo $AUDITORIA_RESPONSE | grep -o '"id":[0-9]*' | head -1 | sed 's/"id"://')

if [ -z "$AUDITORIA_ID" ]; then
    echo -e "${VERMELHO}Aviso: Não foi possível criar auditoria. Verificando se já existe...${RESET}"
    AUDITORIAS_RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" ${BASE_URL}/api/auditorias)
    AUDITORIA_ID=$(echo $AUDITORIAS_RESPONSE | grep -o '"id":[0-9]*' | head -1 | sed 's/"id"://')
    
    if [ -z "$AUDITORIA_ID" ]; then
        echo -e "${VERMELHO}Erro: Não foi possível obter ID de auditoria.${RESET}"
        AUDITORIA_ID=1
    else
        echo -e "${VERDE}✓ Utilizando auditoria existente com ID: $AUDITORIA_ID${RESET}"
    fi
else
    echo -e "${VERDE}✓ Auditoria criada com ID: $AUDITORIA_ID${RESET}"
fi

# 3.3 Criar uma conformidade
echo -e "\n${AMARELO}Criando conformidade de teste...${RESET}"
CONFORMIDADE_RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{
        \"auditoriaId\": $AUDITORIA_ID,
        \"normaAmbientalId\": $NORMA_ID,
        \"descricao\": \"Conformidade de Teste Postman\",
        \"estaConforme\": false,
        \"observacoes\": \"Criada para testes da API\"
    }" \
    ${BASE_URL}/api/conformidades)

CONFORMIDADE_ID=$(echo $CONFORMIDADE_RESPONSE | grep -o '"id":[0-9]*' | head -1 | sed 's/"id"://')

if [ -z "$CONFORMIDADE_ID" ]; then
    echo -e "${VERMELHO}Aviso: Não foi possível criar conformidade. Verificando se já existe...${RESET}"
    CONFORMIDADES_RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" ${BASE_URL}/api/conformidades)
    CONFORMIDADE_ID=$(echo $CONFORMIDADES_RESPONSE | grep -o '"id":[0-9]*' | head -1 | sed 's/"id"://')
    
    if [ -z "$CONFORMIDADE_ID" ]; then
        echo -e "${VERMELHO}Erro: Não foi possível obter ID de conformidade.${RESET}"
        CONFORMIDADE_ID=1
    else
        echo -e "${VERDE}✓ Utilizando conformidade existente com ID: $CONFORMIDADE_ID${RESET}"
    fi
else
    echo -e "${VERDE}✓ Conformidade criada com ID: $CONFORMIDADE_ID${RESET}"
fi

# 3.4 Criar uma pendência
echo -e "\n${AMARELO}Criando pendência de teste...${RESET}"
DATA_PRAZO=$(date -v+30d +"%Y-%m-%d")
PENDENCIA_RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{
        \"conformidadeId\": $CONFORMIDADE_ID,
        \"descricao\": \"Pendência de Teste Postman\",
        \"dataPrazo\": \"$DATA_PRAZO\",
        \"resolvida\": false,
        \"observacoes\": \"Criada para testes da API\"
    }" \
    ${BASE_URL}/api/pendencias)

PENDENCIA_ID=$(echo $PENDENCIA_RESPONSE | grep -o '"id":[0-9]*' | head -1 | sed 's/"id"://')

if [ -z "$PENDENCIA_ID" ]; then
    echo -e "${VERMELHO}Aviso: Não foi possível criar pendência. Verificando se já existe...${RESET}"
    PENDENCIAS_RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" ${BASE_URL}/api/pendencias)
    PENDENCIA_ID=$(echo $PENDENCIAS_RESPONSE | grep -o '"id":[0-9]*' | head -1 | sed 's/"id"://')
    
    if [ -z "$PENDENCIA_ID" ]; then
        echo -e "${VERMELHO}Erro: Não foi possível obter ID de pendência.${RESET}"
        PENDENCIA_ID=1
    else
        echo -e "${VERDE}✓ Utilizando pendência existente com ID: $PENDENCIA_ID${RESET}"
    fi
else
    echo -e "${VERDE}✓ Pendência criada com ID: $PENDENCIA_ID${RESET}"
fi

# 3.5 Criar um log de conformidade
echo -e "\n${AMARELO}Criando log de conformidade de teste...${RESET}"
LOG_RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{
        \"conformidadeId\": $CONFORMIDADE_ID,
        \"acao\": \"CRIACAO\",
        \"detalhes\": \"Log criado para testes da API\"
    }" \
    ${BASE_URL}/api/logs)

LOG_ID=$(echo $LOG_RESPONSE | grep -o '"id":[0-9]*' | head -1 | sed 's/"id"://')

if [ -z "$LOG_ID" ]; then
    echo -e "${VERMELHO}Aviso: Não foi possível criar log. Verificando se já existe...${RESET}"
    LOGS_RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" ${BASE_URL}/api/logs)
    LOG_ID=$(echo $LOGS_RESPONSE | grep -o '"id":[0-9]*' | head -1 | sed 's/"id"://')
    
    if [ -z "$LOG_ID" ]; then
        echo -e "${VERMELHO}Erro: Não foi possível obter ID de log.${RESET}"
        LOG_ID=1
    else
        echo -e "${VERDE}✓ Utilizando log existente com ID: $LOG_ID${RESET}"
    fi
else
    echo -e "${VERDE}✓ Log criado com ID: $LOG_ID${RESET}"
fi

# 4. Atualizar arquivo de variáveis de ambiente do Postman
echo -e "\n${AMARELO}Atualizando variáveis de ambiente do Postman...${RESET}"

# Ler o arquivo de ambiente
AMBIENTE_FILE="./verdicomply-api-environment.json"
if [ ! -f "$AMBIENTE_FILE" ]; then
    echo -e "${VERMELHO}Erro: Arquivo de ambiente não encontrado: $AMBIENTE_FILE${RESET}"
    exit 1
fi

# Criar um arquivo temporário para as modificações
TMP_FILE=$(mktemp)

# Substituir os valores das variáveis com os IDs obtidos
cat "$AMBIENTE_FILE" | \
    sed "s/\"value\": \"\", \"type\": \"default\", \"enabled\": true, \"key\": \"lastCreatedNormaId\"/\"value\": \"$NORMA_ID\", \"type\": \"default\", \"enabled\": true, \"key\": \"lastCreatedNormaId\"/" | \
    sed "s/\"value\": \"\", \"type\": \"default\", \"enabled\": true, \"key\": \"lastCreatedAuditoriaId\"/\"value\": \"$AUDITORIA_ID\", \"type\": \"default\", \"enabled\": true, \"key\": \"lastCreatedAuditoriaId\"/" | \
    sed "s/\"value\": \"\", \"type\": \"default\", \"enabled\": true, \"key\": \"lastCreatedConformidadeId\"/\"value\": \"$CONFORMIDADE_ID\", \"type\": \"default\", \"enabled\": true, \"key\": \"lastCreatedConformidadeId\"/" | \
    sed "s/\"value\": \"\", \"type\": \"default\", \"enabled\": true, \"key\": \"lastCreatedPendenciaId\"/\"value\": \"$PENDENCIA_ID\", \"type\": \"default\", \"enabled\": true, \"key\": \"lastCreatedPendenciaId\"/" | \
    sed "s/\"value\": \"\", \"type\": \"default\", \"enabled\": true, \"key\": \"lastCreatedLogId\"/\"value\": \"$LOG_ID\", \"type\": \"default\", \"enabled\": true, \"key\": \"lastCreatedLogId\"/" > "$TMP_FILE"

# Substituir o arquivo original
mv "$TMP_FILE" "$AMBIENTE_FILE"

echo -e "${VERDE}✓ Variáveis de ambiente atualizadas com sucesso.${RESET}"

# 5. Resumo dos recursos criados
echo -e "\n${AMARELO}=== RESUMO DOS RECURSOS CRIADOS ===${RESET}"
echo -e "Norma Ambiental ID: ${VERDE}$NORMA_ID${RESET}"
echo -e "Auditoria ID: ${VERDE}$AUDITORIA_ID${RESET}"
echo -e "Conformidade ID: ${VERDE}$CONFORMIDADE_ID${RESET}"
echo -e "Pendência ID: ${VERDE}$PENDENCIA_ID${RESET}"
echo -e "Log ID: ${VERDE}$LOG_ID${RESET}"

echo -e "\n${VERDE}✓ Ambiente preparado com sucesso para os testes Postman!${RESET}"
echo -e "${AMARELO}Você pode agora executar: ./run_tests.sh${RESET}"
