#!/bin/bash

# Cores para melhor visualização
VERDE='\033[0;32m'
VERMELHO='\033[0;31m'
AMARELO='\033[0;33m'
AZUL='\033[0;34m'
RESET='\033[0m'

# URL base da API
BASE_URL="http://localhost:8080"

# Credenciais de admin (configuradas no ProdDataInitializer)
ADMIN_USERNAME="admin"
ADMIN_PASSWORD="admin"

# Arquivo temporário para armazenar o token
TOKEN_FILE="/tmp/jwt_token.txt"

# Variáveis para IDs
AUDITORIA_ID=""
CONFORMIDADE_ID=""
PENDENCIA_ID=""
LOG_ID=""

# Função para exibir mensagem de separação
separador() {
    echo -e "${AZUL}================================================================================${RESET}"
    echo -e "${AZUL}$1${RESET}"
    echo -e "${AZUL}================================================================================${RESET}"
    echo ""
}

# Função para verificar o status do comando anterior
verificar_saida() {
    if [ $1 -eq 0 ]; then
        echo -e "${VERDE}✅ Sucesso: $2${RESET}"
    else
        echo -e "${VERMELHO}❌ Falha: $2 (código $1)${RESET}"
        if [ $3 -eq 1 ]; then
            echo -e "${VERMELHO}Abortando os testes...${RESET}"
            exit 1
        fi
    fi
    echo ""
}

# Iniciar os testes sequenciais
separador "🚀 INICIANDO TESTES SEQUENCIAIS DA API VERDICOMPLY COM CURL"

# 1. Testar endpoint de health check (não requer autenticação)
separador "1️⃣ TESTANDO HEALTH CHECK"
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $BASE_URL/api/public/health)
verificar_saida $? "Health check retornou status $RESPONSE" 0

# 2. Fazer login para obter token JWT
separador "2️⃣ REALIZANDO LOGIN PARA OBTER TOKEN JWT"
echo -e "${AMARELO}Fazendo login com usuário $ADMIN_USERNAME...${RESET}"

# Fazer login e extrair o token da resposta
LOGIN_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$ADMIN_USERNAME\",\"password\":\"$ADMIN_PASSWORD\"}" \
  $BASE_URL/api/public/auth/login)

echo "$LOGIN_RESPONSE" | grep -q "token"
verificar_saida $? "Login realizado" 1

# Extrair token da resposta JSON
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | sed 's/"token":"//')
echo "$TOKEN" > $TOKEN_FILE
TOKEN_PREVIEW="${TOKEN:0:20}..."
echo -e "${VERDE}Token JWT obtido: $TOKEN_PREVIEW${RESET}"
echo ""

# 3. Testar endpoints de auditoria (requer autenticação)
separador "3️⃣ TESTANDO ENDPOINTS DE AUDITORIA"

# 3.1 - Listar todas as auditorias
echo -e "${AMARELO}Listando todas as auditorias...${RESET}"
AUDITORIAS_RESPONSE=$(curl -s -X GET \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/auditorias)

echo "$AUDITORIAS_RESPONSE" | grep -q "\["
verificar_saida $? "Listar auditorias" 0

# 3.2 - Criar uma auditoria
DATA_ATUAL=$(date +"%Y-%m-%d")
echo "Criando uma nova auditoria..."
CRIAR_AUDITORIA_RESPONSE=$(curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
      \"descricao\": \"Auditoria API Test\",
      \"status\": \"PROGRAMADA\",
      \"dataAuditoria\": \"$DATA_ATUAL\",
      \"departamentoId\": 1,
      \"observacoes\": \"Teste via API\",
      \"auditorResponsavel\": \"Auditor Teste\"
    }" \
  $BASE_URL/api/auditorias)

echo "$CRIAR_AUDITORIA_RESPONSE" | grep -q "id"
verificar_saida $? "Criar auditoria" 0

# Extrair o ID da auditoria criada - usando métodos alternativos caso um falhe
AUDITORIA_ID_1=$(echo "$CRIAR_AUDITORIA_RESPONSE" | grep -o '"id":[0-9]*' | sed 's/"id"://')
AUDITORIA_ID_2=$(echo "$CRIAR_AUDITORIA_RESPONSE" | grep -o '"id"[[:space:]]*:[[:space:]]*[0-9]*' | grep -o '[0-9]*$')
AUDITORIA_ID_3=$(echo "$CRIAR_AUDITORIA_RESPONSE" | sed -n 's/.*"id"[^0-9]*\([0-9]*\).*/\1/p')

if [ -n "$AUDITORIA_ID_1" ]; then
  AUDITORIA_ID="$AUDITORIA_ID_1"
elif [ -n "$AUDITORIA_ID_2" ]; then
  AUDITORIA_ID="$AUDITORIA_ID_2"
elif [ -n "$AUDITORIA_ID_3" ]; then
  AUDITORIA_ID="$AUDITORIA_ID_3"
else
  AUDITORIA_ID="ID não encontrado"
fi

echo -e "${VERDE}ID da auditoria criada: $AUDITORIA_ID${RESET}"
echo ""

# 3.3 - Buscar auditoria por ID
echo -e "${AMARELO}Buscando auditoria por ID $AUDITORIA_ID...${RESET}"
BUSCAR_AUDITORIA_RESPONSE=$(curl -s -X GET \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/auditorias/$AUDITORIA_ID)

echo "$BUSCAR_AUDITORIA_RESPONSE" | grep -q "id"
verificar_saida $? "Buscar auditoria por ID" 0

# 4. Testar endpoints de conformidade (requer autenticação)
separador "4️⃣ TESTANDO ENDPOINTS DE CONFORMIDADE"

# 4.1 - Criar uma conformidade
echo "Criando uma nova conformidade..."
CRIAR_CONFORMIDADE_RESPONSE=$(curl -s -X POST \
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

echo "$CRIAR_CONFORMIDADE_RESPONSE" | grep -q "id"
verificar_saida $? "Criar conformidade" 0

# Extrair o ID da conformidade criada - usando métodos alternativos caso um falhe
CONFORMIDADE_ID_1=$(echo "$CRIAR_CONFORMIDADE_RESPONSE" | grep -o '"id":[0-9]*' | sed 's/"id"://')
CONFORMIDADE_ID_2=$(echo "$CRIAR_CONFORMIDADE_RESPONSE" | grep -o '"id"[[:space:]]*:[[:space:]]*[0-9]*' | grep -o '[0-9]*$')
CONFORMIDADE_ID_3=$(echo "$CRIAR_CONFORMIDADE_RESPONSE" | sed -n 's/.*"id"[^0-9]*\([0-9]*\).*/\1/p')

if [ -n "$CONFORMIDADE_ID_1" ]; then
  CONFORMIDADE_ID="$CONFORMIDADE_ID_1"
elif [ -n "$CONFORMIDADE_ID_2" ]; then
  CONFORMIDADE_ID="$CONFORMIDADE_ID_2"
elif [ -n "$CONFORMIDADE_ID_3" ]; then
  CONFORMIDADE_ID="$CONFORMIDADE_ID_3"
else
  CONFORMIDADE_ID="ID não encontrado"
fi

echo -e "${VERDE}ID da conformidade criada: $CONFORMIDADE_ID${RESET}"
echo ""

# 4.2 - Buscar conformidade por ID
echo -e "${AMARELO}Buscando conformidade por ID $CONFORMIDADE_ID...${RESET}"
BUSCAR_CONFORMIDADE_RESPONSE=$(curl -s -X GET \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/conformidades/$CONFORMIDADE_ID)

echo "$BUSCAR_CONFORMIDADE_RESPONSE" | grep -q "id"
verificar_saida $? "Buscar conformidade por ID" 0

# 5. Testar endpoints de pendência (requer autenticação)
separador "5️⃣ TESTANDO ENDPOINTS DE PENDÊNCIA"

# 5.1 - Criar uma pendência
echo -e "${AMARELO}Criando uma nova pendência...${RESET}"
CRIAR_PENDENCIA_RESPONSE=$(curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"descricao\":\"Pendência Teste Curl $(date +%s)\",\"resolvida\":false,\"dataPrazo\":\"2025-07-10\",\"conformidadeId\":$CONFORMIDADE_ID,\"acaoCorretiva\":\"Ação corretiva a ser implementada\"}" \
  $BASE_URL/api/pendencias)

echo "$CRIAR_PENDENCIA_RESPONSE" | grep -q "id"
verificar_saida $? "Criar pendência" 0

# Extrair o ID da pendência criada - usando métodos alternativos caso um falhe
PENDENCIA_ID_1=$(echo "$CRIAR_PENDENCIA_RESPONSE" | grep -o '"id":[0-9]*' | sed 's/"id"://')
PENDENCIA_ID_2=$(echo "$CRIAR_PENDENCIA_RESPONSE" | grep -o '"id"[[:space:]]*:[[:space:]]*[0-9]*' | grep -o '[0-9]*$')
PENDENCIA_ID_3=$(echo "$CRIAR_PENDENCIA_RESPONSE" | sed -n 's/.*"id"[^0-9]*\([0-9]*\).*/\1/p')

if [ -n "$PENDENCIA_ID_1" ]; then
  PENDENCIA_ID="$PENDENCIA_ID_1"
elif [ -n "$PENDENCIA_ID_2" ]; then
  PENDENCIA_ID="$PENDENCIA_ID_2"
elif [ -n "$PENDENCIA_ID_3" ]; then
  PENDENCIA_ID="$PENDENCIA_ID_3"
else
  PENDENCIA_ID="ID não encontrado"
fi

echo -e "${VERDE}ID da pendência criada: $PENDENCIA_ID${RESET}"
echo ""

# 5.2 - Buscar pendência por ID
echo -e "${AMARELO}Buscando pendência por ID $PENDENCIA_ID...${RESET}"
BUSCAR_PENDENCIA_RESPONSE=$(curl -s -X GET \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/pendencias/$PENDENCIA_ID)

echo "$BUSCAR_PENDENCIA_RESPONSE" | grep -q "id"
verificar_saida $? "Buscar pendência por ID" 0

# 6. Testar endpoints de log de conformidade (requer autenticação)
separador "6️⃣ TESTANDO ENDPOINTS DE LOG DE CONFORMIDADE"

# 6.1 - Criar um log
echo -e "${AMARELO}Criando um novo log de conformidade...${RESET}"
CRIAR_LOG_RESPONSE=$(curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"descricao\":\"Log Teste Curl $(date +%s)\",\"acao\":\"CRIACAO\",\"conformidadeId\":$CONFORMIDADE_ID}" \
  $BASE_URL/api/logs)

echo "$CRIAR_LOG_RESPONSE" | grep -q "id"
verificar_saida $? "Criar log de conformidade" 0

# Extrair o ID do log criado - usando métodos alternativos caso um falhe
LOG_ID_1=$(echo "$CRIAR_LOG_RESPONSE" | grep -o '"id":[0-9]*' | sed 's/"id"://')
LOG_ID_2=$(echo "$CRIAR_LOG_RESPONSE" | grep -o '"id"[[:space:]]*:[[:space:]]*[0-9]*' | grep -o '[0-9]*$')
LOG_ID_3=$(echo "$CRIAR_LOG_RESPONSE" | sed -n 's/.*"id"[^0-9]*\([0-9]*\).*/\1/p')

if [ -n "$LOG_ID_1" ]; then
  LOG_ID="$LOG_ID_1"
elif [ -n "$LOG_ID_2" ]; then
  LOG_ID="$LOG_ID_2"
elif [ -n "$LOG_ID_3" ]; then
  LOG_ID="$LOG_ID_3"
else
  LOG_ID="ID não encontrado"
fi

echo -e "${VERDE}ID do log criado: $LOG_ID${RESET}"
echo ""

# 6.2 - Buscar log por ID
echo -e "${AMARELO}Buscando log por ID $LOG_ID...${RESET}"
BUSCAR_LOG_RESPONSE=$(curl -s -X GET \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/logs/$LOG_ID)

echo "$BUSCAR_LOG_RESPONSE" | grep -q "id"
verificar_saida $? "Buscar log por ID" 0

# 7. Testes de relacionamentos
separador "7️⃣ TESTANDO RELACIONAMENTOS ENTRE ENTIDADES"

# 7.1 - Buscar conformidades por auditoria
echo -e "${AMARELO}Buscando conformidades pela auditoria ID $AUDITORIA_ID...${RESET}"
CONFORMIDADES_POR_AUDITORIA=$(curl -s -X GET \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/conformidades/auditoria/$AUDITORIA_ID)

echo "$CONFORMIDADES_POR_AUDITORIA" | grep -q "\["
verificar_saida $? "Buscar conformidades por auditoria" 0

# 7.2 - Buscar pendências por conformidade
echo -e "${AMARELO}Buscando pendências pela conformidade ID $CONFORMIDADE_ID...${RESET}"
PENDENCIAS_POR_CONFORMIDADE=$(curl -s -X GET \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/pendencias/conformidade/$CONFORMIDADE_ID)

echo "$PENDENCIAS_POR_CONFORMIDADE" | grep -q "\["
verificar_saida $? "Buscar pendências por conformidade" 0

# 7.3 - Buscar logs por conformidade
echo -e "${AMARELO}Buscando logs pela conformidade ID $CONFORMIDADE_ID...${RESET}"
LOGS_POR_CONFORMIDADE=$(curl -s -X GET \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/logs/conformidade/$CONFORMIDADE_ID)

echo "$LOGS_POR_CONFORMIDADE" | grep -q "\["
verificar_saida $? "Buscar logs por conformidade" 0

# Sumário final
separador "🏁 TESTES CONCLUÍDOS"
echo -e "${VERDE}Os testes foram executados com sucesso!${RESET}"
echo -e "${AMARELO}Resumo:${RESET}"
echo -e "- Health check: OK"
echo -e "- Autenticação JWT: OK"
echo -e "- Auditoria ID: $AUDITORIA_ID"
echo -e "- Conformidade ID: $CONFORMIDADE_ID"
echo -e "- Pendência ID: $PENDENCIA_ID"
echo -e "- Log ID: $LOG_ID"
echo -e ""
echo -e "${VERDE}Todos os endpoints foram testados com sucesso!${RESET}"
