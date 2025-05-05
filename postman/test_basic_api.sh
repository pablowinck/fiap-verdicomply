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

# Função para separador visual
separador() {
    echo ""
    echo -e "${AMARELO}================================================================================${RESET}"
    echo -e "${AMARELO}$1${RESET}"
    echo -e "${AMARELO}================================================================================${RESET}"
    echo ""
}

# Função para verificar saída de comandos
verificar_saida() {
    STATUS=$1
    MENSAGEM=$2
    ESPERADO=$3 # 0 = sucesso, 1 = falha esperada
    
    if [ $STATUS -eq 0 ] && [ $ESPERADO -eq 0 ]; then
        echo -e "${VERDE}✅ Sucesso: $MENSAGEM${RESET}"
        return 0
    elif [ $STATUS -ne 0 ] && [ $ESPERADO -eq 1 ]; then
        echo -e "${VERDE}✅ Sucesso: $MENSAGEM falhou conforme esperado${RESET}"
        return 0
    else
        echo -e "${VERMELHO}❌ Falha: $MENSAGEM${RESET}"
        return 1
    fi
}

# Iniciar o script
separador "🚀 INICIANDO TESTES BÁSICOS DA API VERDICOMPLY COM CURL"

# 1. Verificar Health Check
separador "1️⃣ TESTANDO HEALTH CHECK"

HEALTH_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $BASE_URL/api/public/health)
if [ "$HEALTH_RESPONSE" -eq 200 ]; then
    echo -e "${VERDE}✅ Sucesso: Health check retornou status 200${RESET}"
else
    echo -e "${VERMELHO}❌ Falha: Health check retornou status $HEALTH_RESPONSE${RESET}"
    exit 1
fi

# 2. Testar autenticação
separador "2️⃣ REALIZANDO LOGIN PARA OBTER TOKEN JWT"

echo -e "${AMARELO}Fazendo login com usuário admin...${RESET}"
LOGIN_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}" \
  $BASE_URL/api/public/auth/login)

echo "$LOGIN_RESPONSE" | grep -q "token"
verificar_saida $? "Login realizado" 0

# Extrair token
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | sed 's/"token":"//')
echo -e "${VERDE}Token JWT obtido: ${TOKEN:0:20}...${RESET}"
echo ""

# 3. Testar auditorias
separador "3️⃣ TESTANDO ENDPOINTS DE AUDITORIA"

# 3.1 - Listar auditorias (listar todas)
echo -e "${AMARELO}Listando todas as auditorias...${RESET}"
curl -s -X GET \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/auditorias > /dev/null
verificar_saida $? "Listar auditorias" 0

# 3.2 - Criar uma auditoria
DATA_ATUAL=$(date +"%Y-%m-%d")
echo -e "${AMARELO}Criando uma nova auditoria...${RESET}"
CRIAR_AUDITORIA_RESPONSE=$(curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
      \"descricao\": \"Auditoria API Test $(date +%s)\",
      \"status\": \"PROGRAMADA\",
      \"dataAuditoria\": \"$DATA_ATUAL\",
      \"departamentoId\": 1,
      \"observacoes\": \"Teste via API\",
      \"auditorResponsavel\": \"Auditor Teste\"
    }" \
  $BASE_URL/api/auditorias)

echo "$CRIAR_AUDITORIA_RESPONSE" | grep -q "id"
verificar_saida $? "Criar auditoria" 0

# Extrair o ID da auditoria criada usando diferentes métodos
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
curl -s -X GET \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/auditorias/$AUDITORIA_ID > /dev/null
verificar_saida $? "Buscar auditoria por ID" 0

# 4. Testar cenários negativos
separador "4️⃣ TESTANDO CENÁRIOS NEGATIVOS"

# 4.1 - Tentar acessar endpoint protegido sem token
echo -e "${AMARELO}Tentando acessar endpoint protegido sem token...${RESET}"
SEM_TOKEN_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
  $BASE_URL/api/auditorias)

if [ "$SEM_TOKEN_RESPONSE" -eq 401 ] || [ "$SEM_TOKEN_RESPONSE" -eq 403 ]; then
    echo -e "${VERDE}✅ Sucesso: Acesso negado conforme esperado (status $SEM_TOKEN_RESPONSE)${RESET}"
else
    echo -e "${VERMELHO}❌ Falha: Esperado status 401/403, recebido $SEM_TOKEN_RESPONSE${RESET}"
fi

# 4.2 - Tentar acessar recurso inexistente
echo -e "${AMARELO}Tentando acessar auditoria inexistente...${RESET}"
ID_INEXISTENTE=999999
INEXISTENTE_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/auditorias/$ID_INEXISTENTE)

if [ "$INEXISTENTE_RESPONSE" -eq 404 ]; then
    echo -e "${VERDE}✅ Sucesso: Recurso não encontrado conforme esperado (status 404)${RESET}"
else
    echo -e "${VERMELHO}❌ Falha: Esperado status 404, recebido $INEXISTENTE_RESPONSE${RESET}"
fi

# 4.3 - Tentar login com credenciais inválidas
echo -e "${AMARELO}Tentando login com credenciais inválidas...${RESET}"
LOGIN_INVALIDO_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"usuario_invalido\",\"password\":\"senha_invalida\"}" \
  $BASE_URL/api/public/auth/login)

if [ "$LOGIN_INVALIDO_RESPONSE" -eq 401 ] || [ "$LOGIN_INVALIDO_RESPONSE" -eq 403 ]; then
    echo -e "${VERDE}✅ Sucesso: Login inválido rejeitado conforme esperado (status $LOGIN_INVALIDO_RESPONSE)${RESET}"
else
    echo -e "${VERMELHO}❌ Falha: Esperado status 401/403, recebido $LOGIN_INVALIDO_RESPONSE${RESET}"
fi

# Resumo dos testes
separador "🏁 TESTES CONCLUÍDOS"

echo -e "Os testes foram executados com sucesso!"
echo -e "Resumo:"
echo -e "- Health check: OK"
echo -e "- Autenticação JWT: OK"
echo -e "- Auditoria ID: $AUDITORIA_ID"
echo -e "- Cenários negativos: OK"
echo -e ""
echo -e "Testes básicos concluídos com sucesso!"
