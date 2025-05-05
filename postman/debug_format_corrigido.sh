#!/bin/bash

# URL base da API
BASE_URL="http://localhost:8080"

# Credenciais de admin
USERNAME="admin"
PASSWORD="admin"

echo "Testando login para verificar formato da resposta..."

# Fazer login e mostrar resposta
LOGIN_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}" \
  $BASE_URL/api/public/auth/login)

echo "Resposta do login:"
echo "$LOGIN_RESPONSE"
echo ""

# Extrair token
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | sed 's/"token":"//')
echo "Token extraído: ${TOKEN:0:20}..."
echo ""

echo "Testando criação de auditoria para verificar formato da resposta..."

# Usar data atual para evitar erro de validação de data futura
DATA_ATUAL=$(date +"%Y-%m-%d")

# Criar auditoria com todos os campos obrigatórios e mostrar resposta
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

echo "Resposta da criação de auditoria:"
echo "$AUDITORIA_RESPONSE"
echo ""

# Tentar extrair ID da resposta JSON usando diferentes métodos
echo "Tentativas de extração do ID:"
echo "1. Usando grep básico:"
ID1=$(echo "$AUDITORIA_RESPONSE" | grep -o '"id":[0-9]*' | sed 's/"id"://')
echo "   Resultado: $ID1"

echo "2. Usando grep mais específico:"
ID2=$(echo "$AUDITORIA_RESPONSE" | grep -o '"id"[[:space:]]*:[[:space:]]*[0-9]*' | grep -o '[0-9]*$')
echo "   Resultado: $ID2"

echo "3. Usando sed:"
ID3=$(echo "$AUDITORIA_RESPONSE" | sed -n 's/.*"id"[^0-9]*\([0-9]*\).*/\1/p')
echo "   Resultado: $ID3"

if [ -n "$ID1" ]; then
  AUDITORIA_ID="$ID1"
elif [ -n "$ID2" ]; then
  AUDITORIA_ID="$ID2"
elif [ -n "$ID3" ]; then
  AUDITORIA_ID="$ID3"
else
  echo "Não foi possível extrair o ID da auditoria."
  exit 1
fi

echo "ID da auditoria extraído: $AUDITORIA_ID"
echo ""

echo "Testando busca da auditoria criada..."
BUSCAR_AUDITORIA=$(curl -s -X GET \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/auditorias/$AUDITORIA_ID)

echo "Resposta da busca da auditoria:"
echo "$BUSCAR_AUDITORIA"
