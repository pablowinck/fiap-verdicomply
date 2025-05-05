#!/bin/bash

# URL base da API
BASE_URL="http://localhost:8080"

# Credenciais de admin
USERNAME="admin"
PASSWORD="admin"

# Cores para output
VERDE="\033[0;32m"
AMARELO="\033[0;33m"
VERMELHO="\033[0;31m"
RESET="\033[0m"

echo -e "${AMARELO}Iniciando depuração de endpoints...${RESET}"

# Fazer login e obter token
echo -e "${AMARELO}Obtendo token JWT...${RESET}"
LOGIN_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}" \
  $BASE_URL/api/public/auth/login)

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | sed 's/"token":"//')
echo -e "${VERDE}Token obtido: ${TOKEN:0:20}...${RESET}"
echo ""

# Usar data atual para evitar erro de validação
DATA_ATUAL=$(date +"%Y-%m-%d")

# Criar auditoria
echo -e "${AMARELO}Criando auditoria...${RESET}"
AUDITORIA_RESPONSE=$(curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
      \"descricao\": \"Auditoria Debug $(date +%s)\",
      \"status\": \"PROGRAMADA\",
      \"dataAuditoria\": \"$DATA_ATUAL\",
      \"departamentoId\": 1,
      \"observacoes\": \"Teste Debug\",
      \"auditorResponsavel\": \"Auditor Teste\"
    }" \
  $BASE_URL/api/auditorias)

echo -e "${VERDE}Resposta da auditoria:${RESET}"
echo "$AUDITORIA_RESPONSE"
echo ""

# Extrair ID da auditoria
AUDITORIA_ID=$(echo "$AUDITORIA_RESPONSE" | grep -o '"id":[0-9]*' | sed 's/"id"://')
echo -e "${VERDE}ID da auditoria: $AUDITORIA_ID${RESET}"
echo ""

if [ -z "$AUDITORIA_ID" ]; then
  echo -e "${VERMELHO}Não foi possível extrair o ID da auditoria. Encerrando.${RESET}"
  exit 1
fi

# Criar conformidade
echo -e "${AMARELO}Criando conformidade...${RESET}"
CONFORMIDADE_RESPONSE=$(curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
      \"auditoriaId\": $AUDITORIA_ID,
      \"normaAmbientalId\": 1,
      \"estaConforme\": \"S\",
      \"descricaoConformidade\": \"Conformidade de teste\",
      \"evidencias\": \"Evidências de conformidade de teste\"
    }" \
  $BASE_URL/api/conformidades)

echo -e "${VERDE}Resposta da conformidade (completa):${RESET}"
echo "$CONFORMIDADE_RESPONSE"
echo ""

echo -e "${AMARELO}Análise de debug para conformidade:${RESET}"
echo "1. Primeiros 100 caracteres: ${CONFORMIDADE_RESPONSE:0:100}..."
echo "2. Tentativa com grep básico: $(echo "$CONFORMIDADE_RESPONSE" | grep -o '"id":[0-9]*' | sed 's/"id"://')"
echo "3. Tentativa com grep específico: $(echo "$CONFORMIDADE_RESPONSE" | grep -o '"id"[[:space:]]*:[[:space:]]*[0-9]*' | grep -o '[0-9]*$')"
echo "4. Tentativa com sed: $(echo "$CONFORMIDADE_RESPONSE" | sed -n 's/.*"id"[^0-9]*\([0-9]*\).*/\1/p')"
echo "5. Tudo com a palavra 'id': $(echo "$CONFORMIDADE_RESPONSE" | grep -o '[^a-zA-Z]id[^a-zA-Z][^,}]*' | head -1)"
echo ""

# Extrair o ID da conformidade usando múltiplas abordagens
echo -e "${AMARELO}Tentando extrair ID de conformidade com abordagens alternativas...${RESET}"
ID_TENTATIVA_1=$(echo "$CONFORMIDADE_RESPONSE" | grep -o -E "[^a-zA-Z0-9_]id[^a-zA-Z0-9_][^,}]*" | head -1 | grep -o -E "[0-9]+" | head -1)
echo "- Tentativa com regex complexo: $ID_TENTATIVA_1"
ID_TENTATIVA_2=$(echo "$CONFORMIDADE_RESPONSE" | grep -o -E "\"id\":[ ]*[0-9]+" | head -1 | grep -o -E "[0-9]+")
echo "- Tentativa com regex específico para JSON: $ID_TENTATIVA_2"
echo ""
