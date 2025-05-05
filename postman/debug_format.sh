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

# Criar auditoria e mostrar resposta
AUDITORIA_RESPONSE=$(curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"descricao\":\"Auditoria Debug $(date +%s)\",\"status\":\"PROGRAMADA\",\"dataAuditoria\":\"2025-06-10\",\"departamentoId\":1,\"observacoes\":\"Teste Debug\"}" \
  $BASE_URL/api/auditorias)

echo "Resposta da criação de auditoria:"
echo "$AUDITORIA_RESPONSE"
echo ""

# Tentar extrair ID da resposta JSON usando diferentes métodos
echo "Tentativas de extração do ID:"
echo "1. Usando grep padrão:"
ID1=$(echo "$AUDITORIA_RESPONSE" | grep -o '"id":[0-9]*' | sed 's/"id"://')
echo "   Resultado: $ID1"

echo "2. Usando grep mais específico:"
ID2=$(echo "$AUDITORIA_RESPONSE" | grep -o '"id"[[:space:]]*:[[:space:]]*[0-9]*' | grep -o '[0-9]*$')
echo "   Resultado: $ID2"

echo "3. Usando sed:"
ID3=$(echo "$AUDITORIA_RESPONSE" | sed -n 's/.*"id"[^0-9]*\([0-9]*\).*/\1/p')
echo "   Resultado: $ID3"

echo "4. Exibindo apenas primeiros 100 caracteres da resposta para análise:"
echo "${AUDITORIA_RESPONSE:0:100}..."
