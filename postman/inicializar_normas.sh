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

echo -e "${AMARELO}=== INICIALIZANDO NORMAS AMBIENTAIS ===${RESET}"

# Obter token JWT para autorização
echo -e "${AMARELO}Obtendo token JWT...${RESET}"
LOGIN_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}" \
  $BASE_URL/api/public/auth/login)

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | sed 's/"token":"//')
echo -e "${VERDE}Token JWT obtido: ${TOKEN:0:20}...${RESET}"
echo ""

# Criar normas ambientais diretamente via JSON
echo -e "${AMARELO}Criando norma ambiental 1 - CONAMA-001...${RESET}"
curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
      "codigoNorma": "CONAMA-001",
      "titulo": "Resolução CONAMA nº 001",
      "descricao": "Critérios básicos e diretrizes para avaliação de impacto ambiental",
      "orgaoFiscalizador": "CONAMA",
      "severidade": "Média"
    }' \
  $BASE_URL/api/normas

echo -e "${AMARELO}Criando norma ambiental 2 - ISO-14001...${RESET}"
curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
      "codigoNorma": "ISO-14001",
      "titulo": "ISO 14001:2015",
      "descricao": "Sistema de Gestão Ambiental",
      "orgaoFiscalizador": "ISO",
      "severidade": "Alta"
    }' \
  $BASE_URL/api/normas

echo -e "${AMARELO}Criando norma ambiental 3 - NBR-10004...${RESET}"
curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
      "codigoNorma": "NBR-10004",
      "titulo": "NBR 10004",
      "descricao": "Classificação de resíduos sólidos",
      "orgaoFiscalizador": "ABNT",
      "severidade": "Alta"
    }' \
  $BASE_URL/api/normas

echo -e "${AMARELO}Criando norma ambiental 4 - LEI-12305...${RESET}"
curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
      "codigoNorma": "LEI-12305",
      "titulo": "Lei 12.305/2010",
      "descricao": "Política Nacional de Resíduos Sólidos",
      "orgaoFiscalizador": "Governo Federal",
      "severidade": "Alta"
    }' \
  $BASE_URL/api/normas

echo -e "${AMARELO}Criando norma ambiental 5 - LEI-9605...${RESET}"
curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
      "codigoNorma": "LEI-9605",
      "titulo": "Lei 9.605/1998",
      "descricao": "Lei de Crimes Ambientais",
      "orgaoFiscalizador": "Governo Federal",
      "severidade": "Alta"
    }' \
  $BASE_URL/api/normas

echo -e "${VERDE}Inicialização de normas ambientais concluída.${RESET}"
