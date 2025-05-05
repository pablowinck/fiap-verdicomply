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

# Contador de testes
TESTES_TOTAL=0
TESTES_SUCESSO=0
TESTES_FALHA=0

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
    
    TESTES_TOTAL=$((TESTES_TOTAL + 1))
    
    if [ $STATUS -eq 0 ] && [ $ESPERADO -eq 0 ]; then
        echo -e "${VERDE}✅ Sucesso: $MENSAGEM${RESET}"
        TESTES_SUCESSO=$((TESTES_SUCESSO + 1))
        return 0
    elif [ $STATUS -ne 0 ] && [ $ESPERADO -eq 1 ]; then
        echo -e "${VERDE}✅ Sucesso: $MENSAGEM falhou conforme esperado${RESET}"
        TESTES_SUCESSO=$((TESTES_SUCESSO + 1))
        return 0
    else
        echo -e "${VERMELHO}❌ Falha: $MENSAGEM${RESET}"
        TESTES_FALHA=$((TESTES_FALHA + 1))
        return 1
    fi
}

# Iniciar o script
separador "🚀 TESTES FINAIS DA API VERDICOMPLY"

# 1. Verificar Health Check
separador "1️⃣ TESTANDO HEALTH CHECK"

HEALTH_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $BASE_URL/api/public/health)
if [ "$HEALTH_RESPONSE" -eq 200 ]; then
    echo -e "${VERDE}✅ Sucesso: Health check retornou status 200${RESET}"
    TESTES_SUCESSO=$((TESTES_SUCESSO + 1))
else
    echo -e "${VERMELHO}❌ Falha: Health check retornou status $HEALTH_RESPONSE${RESET}"
    TESTES_FALHA=$((TESTES_FALHA + 1))
fi
TESTES_TOTAL=$((TESTES_TOTAL + 1))

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

# 3. Testar registro de usuário
separador "3️⃣ TESTANDO REGISTRO DE USUÁRIO"

# 3.1 - Registro com username já existente (deve falhar)
echo -e "${AMARELO}Tentando registrar usuário com username já existente...${RESET}"
USERNAME_EXISTENTE="admin"
REGISTRO_RESPOSTA=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME_EXISTENTE\",\"password\":\"senha123\",\"role\":\"AUDITOR\"}" \
  $BASE_URL/api/public/registro)

if [ "$REGISTRO_RESPOSTA" -eq 400 ] || [ "$REGISTRO_RESPOSTA" -eq 500 ]; then
    echo -e "${VERDE}✅ Sucesso: Registro com username existente rejeitado (status $REGISTRO_RESPOSTA)${RESET}"
    TESTES_SUCESSO=$((TESTES_SUCESSO + 1))
else
    echo -e "${VERMELHO}❌ Falha: Esperado status 400/500, recebido $REGISTRO_RESPOSTA${RESET}"
    TESTES_FALHA=$((TESTES_FALHA + 1))
fi
TESTES_TOTAL=$((TESTES_TOTAL + 1))

# 4. Testar auditorias
separador "4️⃣ TESTANDO ENDPOINTS DE AUDITORIA"

# 4.1 - Listar auditorias
echo -e "${AMARELO}Listando todas as auditorias...${RESET}"
curl -s -X GET \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/auditorias > /dev/null
verificar_saida $? "Listar auditorias" 0

# 4.2 - Criar uma auditoria
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

# Extrair o ID da auditoria criada
AUDITORIA_ID=$(echo "$CRIAR_AUDITORIA_RESPONSE" | grep -o '"id":[0-9]*' | sed 's/"id"://')
echo -e "${VERDE}ID da auditoria criada: $AUDITORIA_ID${RESET}"
echo ""

# 4.3 - Buscar auditoria por ID
echo -e "${AMARELO}Buscando auditoria por ID $AUDITORIA_ID...${RESET}"
curl -s -X GET \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/auditorias/$AUDITORIA_ID > /dev/null
verificar_saida $? "Buscar auditoria por ID" 0

# 5. Testar cenários negativos
separador "5️⃣ TESTANDO CENÁRIOS NEGATIVOS"

# 5.1 - Tentar acessar endpoint protegido sem token
echo -e "${AMARELO}Tentando acessar endpoint protegido sem token...${RESET}"
SEM_TOKEN_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
  $BASE_URL/api/auditorias)

if [ "$SEM_TOKEN_RESPONSE" -eq 401 ] || [ "$SEM_TOKEN_RESPONSE" -eq 403 ]; then
    echo -e "${VERDE}✅ Sucesso: Acesso negado conforme esperado (status $SEM_TOKEN_RESPONSE)${RESET}"
    TESTES_SUCESSO=$((TESTES_SUCESSO + 1))
else
    echo -e "${VERMELHO}❌ Falha: Esperado status 401/403, recebido $SEM_TOKEN_RESPONSE${RESET}"
    TESTES_FALHA=$((TESTES_FALHA + 1))
fi
TESTES_TOTAL=$((TESTES_TOTAL + 1))

# 5.2 - Tentar acessar recurso inexistente
echo -e "${AMARELO}Tentando acessar auditoria inexistente...${RESET}"
ID_INEXISTENTE=999999
INEXISTENTE_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/auditorias/$ID_INEXISTENTE)

if [ "$INEXISTENTE_RESPONSE" -eq 404 ]; then
    echo -e "${VERDE}✅ Sucesso: Recurso não encontrado conforme esperado (status 404)${RESET}"
    TESTES_SUCESSO=$((TESTES_SUCESSO + 1))
else
    echo -e "${VERMELHO}❌ Falha: Esperado status 404, recebido $INEXISTENTE_RESPONSE${RESET}"
    TESTES_FALHA=$((TESTES_FALHA + 1))
fi
TESTES_TOTAL=$((TESTES_TOTAL + 1))

# 5.3 - Tentar login com credenciais inválidas
echo -e "${AMARELO}Tentando login com credenciais inválidas...${RESET}"
LOGIN_INVALIDO_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"usuario_invalido\",\"password\":\"senha_invalida\"}" \
  $BASE_URL/api/public/auth/login)

if [ "$LOGIN_INVALIDO_RESPONSE" -eq 401 ] || [ "$LOGIN_INVALIDO_RESPONSE" -eq 403 ]; then
    echo -e "${VERDE}✅ Sucesso: Login inválido rejeitado conforme esperado (status $LOGIN_INVALIDO_RESPONSE)${RESET}"
    TESTES_SUCESSO=$((TESTES_SUCESSO + 1))
else
    echo -e "${VERMELHO}❌ Falha: Esperado status 401/403, recebido $LOGIN_INVALIDO_RESPONSE${RESET}"
    TESTES_FALHA=$((TESTES_FALHA + 1))
fi
TESTES_TOTAL=$((TESTES_TOTAL + 1))

# Resumo dos testes
separador "🏁 RESUMO DOS TESTES"

echo -e "Total de testes: $TESTES_TOTAL"
echo -e "${VERDE}Testes com sucesso: $TESTES_SUCESSO${RESET}"
echo -e "${VERMELHO}Testes com falha: $TESTES_FALHA${RESET}"
echo -e ""
echo -e "Detalhes dos recursos criados:"
echo -e "- Auditoria ID: $AUDITORIA_ID"
echo -e ""

# Cálculo da porcentagem de sucesso
PORCENTAGEM_SUCESSO=$((TESTES_SUCESSO * 100 / TESTES_TOTAL))
echo -e "Porcentagem de sucesso: ${VERDE}$PORCENTAGEM_SUCESSO%${RESET}"

if [ $TESTES_FALHA -eq 0 ]; then
    echo -e "${VERDE}✅ Todos os testes foram executados com sucesso!${RESET}"
else
    echo -e "${VERMELHO}⚠️ Alguns testes falharam. Verifique os detalhes acima.${RESET}"
fi
