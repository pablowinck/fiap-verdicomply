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
separador "🚀 INICIANDO TESTES DE CONFORMIDADES DA API VERDICOMPLY"

# 1. Obter token JWT para autorização
separador "1️⃣ OBTENDO TOKEN JWT"

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

# 2. Criar Auditoria (pré-requisito para Conformidade)
separador "2️⃣ CRIANDO AUDITORIA (PRÉ-REQUISITO)"

DATA_ATUAL=$(date +"%Y-%m-%d")
echo -e "${AMARELO}Criando uma nova auditoria...${RESET}"
CRIAR_AUDITORIA_RESPONSE=$(curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
      \"descricao\": \"Auditoria para testes de conformidade\",
      \"status\": \"PROGRAMADA\",
      \"dataAuditoria\": \"$DATA_ATUAL\",
      \"departamentoId\": 1,
      \"observacoes\": \"Teste de conformidades\",
      \"auditorResponsavel\": \"Auditor Teste\"
    }" \
  $BASE_URL/api/auditorias)

echo "$CRIAR_AUDITORIA_RESPONSE" | grep -q "id"
verificar_saida $? "Criar auditoria" 0

# Extrair o ID da auditoria criada
AUDITORIA_ID=$(echo "$CRIAR_AUDITORIA_RESPONSE" | grep -o '"id":[0-9]*' | sed 's/"id"://')
echo -e "${VERDE}ID da auditoria criada: $AUDITORIA_ID${RESET}"
echo ""

# 3. Listar Normas Ambientais
separador "3️⃣ LISTANDO NORMAS AMBIENTAIS"

echo -e "${AMARELO}Buscando normas ambientais...${RESET}"
NORMAS_RESPONSE=$(curl -s -X GET \
  -H "Authorization: Bearer $TOKEN" \
  $BASE_URL/api/normas)

echo -e "${VERDE}Resposta da busca de normas:${RESET}"
echo "$NORMAS_RESPONSE"
echo ""

# 4. Criar Conformidade
separador "4️⃣ CRIANDO CONFORMIDADE"

echo -e "${AMARELO}Criando uma nova conformidade...${RESET}"
CRIAR_CONFORMIDADE_RESPONSE=$(curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
      \"auditoriaId\": $AUDITORIA_ID,
      \"normaAmbientalId\": 1,
      \"estaConforme\": \"S\",
      \"descricaoConformidade\": \"Conformidade para testes\",
      \"evidencias\": \"Evidências de conformidade para testes\"
    }" \
  $BASE_URL/api/conformidades)

echo -e "${VERDE}Resposta da criação de conformidade:${RESET}"
echo "$CRIAR_CONFORMIDADE_RESPONSE"
echo ""

# Verificar se foi bem-sucedido
if echo "$CRIAR_CONFORMIDADE_RESPONSE" | grep -q "id"; then
    echo -e "${VERDE}✅ Sucesso: Conformidade criada${RESET}"
    
    # Extrair o ID da conformidade criada
    CONFORMIDADE_ID=$(echo "$CRIAR_CONFORMIDADE_RESPONSE" | grep -o '"id":[0-9]*' | sed 's/"id"://')
    echo -e "${VERDE}ID da conformidade criada: $CONFORMIDADE_ID${RESET}"
    
    # 5. Buscar conformidade por ID
    separador "5️⃣ BUSCANDO CONFORMIDADE POR ID"
    
    echo -e "${AMARELO}Buscando conformidade por ID $CONFORMIDADE_ID...${RESET}"
    BUSCAR_CONFORMIDADE=$(curl -s -X GET \
      -H "Authorization: Bearer $TOKEN" \
      $BASE_URL/api/conformidades/$CONFORMIDADE_ID)
    
    echo -e "${VERDE}Resposta da busca de conformidade:${RESET}"
    echo "$BUSCAR_CONFORMIDADE"
    echo ""
    
    # 6. Atualizar conformidade
    separador "6️⃣ ATUALIZANDO CONFORMIDADE"
    
    echo -e "${AMARELO}Atualizando conformidade...${RESET}"
    ATUALIZAR_CONFORMIDADE=$(curl -s -X PUT \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json" \
      -d "{
          \"auditoriaId\": $AUDITORIA_ID,
          \"normaAmbientalId\": 1,
          \"estaConforme\": \"N\",
          \"descricaoConformidade\": \"Conformidade atualizada\",
          \"evidencias\": \"Evidências de conformidade atualizadas\"
        }" \
      $BASE_URL/api/conformidades/$CONFORMIDADE_ID)
    
    echo -e "${VERDE}Resposta da atualização de conformidade:${RESET}"
    echo "$ATUALIZAR_CONFORMIDADE"
    echo ""
    
    # 7. Criar pendência para conformidade
    separador "7️⃣ CRIANDO PENDÊNCIA PARA CONFORMIDADE"
    
    echo -e "${AMARELO}Criando uma nova pendência...${RESET}"
    CRIAR_PENDENCIA_RESPONSE=$(curl -s -X POST \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json" \
      -d "{
          \"conformidadeId\": $CONFORMIDADE_ID,
          \"descricaoPendencia\": \"Pendência para testes\",
          \"prazoResolucao\": \"2025-12-31\",
          \"resolvida\": \"N\",
          \"observacoes\": \"Observações da pendência para testes\"
        }" \
      $BASE_URL/api/pendencias)
    
    echo -e "${VERDE}Resposta da criação de pendência:${RESET}"
    echo "$CRIAR_PENDENCIA_RESPONSE"
    echo ""
    
    # Extrair o ID da pendência criada
    if echo "$CRIAR_PENDENCIA_RESPONSE" | grep -q "id"; then
        PENDENCIA_ID=$(echo "$CRIAR_PENDENCIA_RESPONSE" | grep -o '"id":[0-9]*' | sed 's/"id"://')
        echo -e "${VERDE}ID da pendência criada: $PENDENCIA_ID${RESET}"
        
        # 8. Buscar pendências por conformidade
        separador "8️⃣ BUSCANDO PENDÊNCIAS POR CONFORMIDADE"
        
        echo -e "${AMARELO}Buscando pendências pela conformidade ID $CONFORMIDADE_ID...${RESET}"
        BUSCAR_PENDENCIAS=$(curl -s -X GET \
          -H "Authorization: Bearer $TOKEN" \
          $BASE_URL/api/pendencias/conformidade/$CONFORMIDADE_ID)
        
        echo -e "${VERDE}Resposta da busca de pendências:${RESET}"
        echo "$BUSCAR_PENDENCIAS"
        echo ""
    else
        echo -e "${VERMELHO}❌ Falha: Não foi possível criar pendência${RESET}"
    fi
    
    # 9. Criar log de conformidade
    separador "9️⃣ CRIANDO LOG DE CONFORMIDADE"
    
    echo -e "${AMARELO}Criando um novo log de conformidade...${RESET}"
    CRIAR_LOG_RESPONSE=$(curl -s -X POST \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json" \
      -d "{
          \"conformidadeId\": $CONFORMIDADE_ID,
          \"acao\": \"ATUALIZAÇÃO\",
          \"detalhes\": \"Log para testes\"
        }" \
      $BASE_URL/api/logs)
    
    echo -e "${VERDE}Resposta da criação de log:${RESET}"
    echo "$CRIAR_LOG_RESPONSE"
    echo ""
    
    # Extrair o ID do log criado
    if echo "$CRIAR_LOG_RESPONSE" | grep -q "id"; then
        LOG_ID=$(echo "$CRIAR_LOG_RESPONSE" | grep -o '"id":[0-9]*' | sed 's/"id"://')
        echo -e "${VERDE}ID do log criado: $LOG_ID${RESET}"
        
        # 10. Buscar logs por conformidade
        separador "🔟 BUSCANDO LOGS POR CONFORMIDADE"
        
        echo -e "${AMARELO}Buscando logs pela conformidade ID $CONFORMIDADE_ID...${RESET}"
        BUSCAR_LOGS=$(curl -s -X GET \
          -H "Authorization: Bearer $TOKEN" \
          $BASE_URL/api/logs/conformidade/$CONFORMIDADE_ID)
        
        echo -e "${VERDE}Resposta da busca de logs:${RESET}"
        echo "$BUSCAR_LOGS"
        echo ""
    else
        echo -e "${VERMELHO}❌ Falha: Não foi possível criar log${RESET}"
    fi
else
    echo -e "${VERMELHO}❌ Falha: Não foi possível criar conformidade${RESET}"
fi

# Resumo dos testes
separador "🏁 TESTES CONCLUÍDOS"

echo -e "Resumo dos testes:"
echo -e "- Auditoria ID: $AUDITORIA_ID"
if [ -n "$CONFORMIDADE_ID" ]; then
    echo -e "- Conformidade ID: $CONFORMIDADE_ID"
    if [ -n "$PENDENCIA_ID" ]; then
        echo -e "- Pendência ID: $PENDENCIA_ID"
    fi
    if [ -n "$LOG_ID" ]; then
        echo -e "- Log ID: $LOG_ID"
    fi
fi
echo ""
echo -e "Testes de conformidades concluídos!"
