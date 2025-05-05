#!/bin/bash

# Script para testar a API VerdiComply em execução no Docker usando cURL
# Testa autenticação e acesso a endpoints protegidos

# Definindo cores para melhor visualização
VERDE='\033[0;32m'
VERMELHO='\033[0;31m'
AMARELO='\033[0;33m'
AZUL='\033[0;34m'
RESET='\033[0m'

# Configurações
API_URL="http://localhost:8080"
LOGIN_ENDPOINT="/api/public/auth/login"
ENDPOINT_PROTEGIDO="/api/auditorias"

# Credenciais (configuradas no TestDataInitializer)
USERNAME="admin"
PASSWORD="admin"

echo -e "${AZUL}================================${RESET}"
echo -e "${AZUL}   TESTE DA API VERDICOMPLY    ${RESET}"
echo -e "${AZUL}================================${RESET}"
echo

# Verificar se a API está no ar
echo -e "${AMARELO}[TESTE 1] Verificando se a API está acessível...${RESET}"
STATUS=$(curl -s -o /dev/null -w "%{http_code}" $API_URL/api/public/health || echo 000)

if [ "$STATUS" = "000" ]; then
    echo -e "${VERMELHO}❌ API não está acessível (falha de conexão)${RESET}"
    echo -e "${VERMELHO}Verifique se o container docker está rodando!${RESET}"
    exit 1
else
    echo -e "${VERDE}✅ API está acessível (status $STATUS)${RESET}"
fi

echo

# Tentativa de acesso a endpoint protegido sem autenticação
echo -e "${AMARELO}[TESTE 2] Tentando acessar endpoint protegido sem autenticação...${RESET}"
STATUS_SEM_AUTH=$(curl -s -o /dev/null -w "%{http_code}" $API_URL$ENDPOINT_PROTEGIDO)

if [ $STATUS_SEM_AUTH -eq 401 ]; then
    echo -e "${VERDE}✅ Acesso negado conforme esperado (status 401)${RESET}"
else
    echo -e "${VERMELHO}❌ Comportamento inesperado! Esperado 401, recebido $STATUS_SEM_AUTH${RESET}"
    exit 1
fi

echo

# Login com as credenciais
echo -e "${AMARELO}[TESTE 3] Realizando login...${RESET}"
RESPONSE=$(curl -s -X POST $API_URL$LOGIN_ENDPOINT \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

# Extrair token da resposta (ajustar conforme necessário)
TOKEN=$(echo $RESPONSE | grep -o '"token":"[^"]*' | sed 's/"token":"//')

if [ -z "$TOKEN" ]; then
    echo -e "${VERMELHO}❌ Falha no login! Resposta: $RESPONSE${RESET}"
    exit 1
else
    echo -e "${VERDE}✅ Login realizado com sucesso!${RESET}"
    echo -e "${AZUL}Token (primeiros 20 caracteres): ${TOKEN:0:20}...${RESET}"
fi

echo

# Acessar endpoint protegido com o token
echo -e "${AMARELO}[TESTE 4] Acessando endpoint protegido com token...${RESET}"
STATUS_COM_AUTH=$(curl -s -o /dev/null -w "%{http_code}" \
    -H "Authorization: Bearer $TOKEN" \
    $API_URL$ENDPOINT_PROTEGIDO)

if [ $STATUS_COM_AUTH -eq 200 ]; then
    echo -e "${VERDE}✅ Acesso autorizado ao endpoint protegido! (status 200)${RESET}"
else
    echo -e "${VERMELHO}❌ Falha ao acessar endpoint protegido! (status $STATUS_COM_AUTH)${RESET}"
    exit 1
fi

echo

# Teste completo
echo -e "${AZUL}================================${RESET}"
echo -e "${VERDE}🎉 Todos os testes completados com sucesso!${RESET}"
echo -e "${AZUL}================================${RESET}"
