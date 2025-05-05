#!/bin/bash

# Script para verificar se todos os endpoints estão cobertos na coleção Postman
# Autor: Pablo Winter
# Data: Agosto 2023

# Cores para formatação
VERDE='\033[0;32m'
VERMELHO='\033[0;31m'
AMARELO='\033[0;33m'
AZUL='\033[0;34m'
RESET='\033[0m'

echo -e "${AMARELO}=== VERIFICANDO COBERTURA DE ENDPOINTS NA COLEÇÃO POSTMAN ===${RESET}"

# Lista de todos os endpoints disponíveis na API
declare -a ENDPOINTS=(
    # Autenticação
    "POST /api/public/auth/login"
    "POST /api/public/registro"
    
    # Health Check
    "GET /api/public/health"
    
    # Normas Ambientais
    "GET /api/normas"
    "GET /api/normas/{id}"
    "GET /api/normas/orgao/{orgaoFiscalizador}"
    "GET /api/normas/codigo/{codigoNorma}"
    "POST /api/normas"
    "PUT /api/normas/{id}"
    "DELETE /api/normas/{id}"
    
    # Auditorias
    "GET /api/auditorias"
    "GET /api/auditorias/{id}"
    "GET /api/auditorias/status/{status}"
    "GET /api/auditorias/departamento/{departamentoId}"
    "POST /api/auditorias"
    "PUT /api/auditorias/{id}"
    "DELETE /api/auditorias/{id}"
    
    # Conformidades
    "GET /api/conformidades"
    "GET /api/conformidades/{id}"
    "GET /api/conformidades/auditoria/{auditoriaId}"
    "GET /api/conformidades/status/{estaConforme}"
    "GET /api/conformidades/norma/{normaAmbientalId}"
    "POST /api/conformidades"
    "PUT /api/conformidades/{id}"
    "DELETE /api/conformidades/{id}"
    
    # Pendências
    "GET /api/pendencias"
    "GET /api/pendencias/{id}"
    "GET /api/pendencias/conformidade/{conformidadeId}"
    "GET /api/pendencias/status/{resolvida}"
    "GET /api/pendencias/vencidas"
    "POST /api/pendencias"
    "PUT /api/pendencias/{id}"
    "DELETE /api/pendencias/{id}"
    
    # Logs de Conformidade
    "GET /api/logs"
    "GET /api/logs/{id}"
    "GET /api/logs/conformidade/{conformidadeId}"
    "GET /api/logs/acao/{acao}"
    "POST /api/logs"
    "DELETE /api/logs/{id}"
)

# Verifica se um método e caminho estão na coleção do Postman
endpoint_exists() {
    local metodo=$1
    local caminho=$2
    local colecao=$3
    
    # Extrai a parte da URL sem parâmetros para facilitar a comparação
    caminho_base=$(echo $caminho | cut -d'{' -f1 | sed 's|/$||')
    
    # Converte o método HTTP para minúsculo para compatibilidade com o formato Postman
    metodo_lower=$(echo $metodo | tr '[:upper:]' '[:lower:]')
    
    # Primeiro tenta encontrar o método HTTP exato
    if grep -q "\"method\": \"$metodo\"" "$colecao"; then
        # Se encontrou o método, verifica se o caminho também está presente
        if grep -q "\"path\": \\[\"api\"" "$colecao" | grep -q "$(echo $caminho_base | sed -e 's|^/api/||' -e 's|/|\\\\/|g')"; then
            return 0  # Endpoint encontrado
        fi
    fi
    
    # Tenta encontrar com método em minúsculo (formato mais comum no Postman)
    if grep -q "\"method\": \"$metodo_lower\"" "$colecao"; then
        # Se encontrou o método, verifica se o caminho também está presente
        if grep -q "\"raw\"" "$colecao" | grep -q "$(echo $caminho_base | sed 's|/|\\\\/|g')"; then
            return 0  # Endpoint encontrado
        fi
    fi
    
    # Verifica de forma mais genérica se o URL contém o caminho base
    raw_url_pattern=$(echo $caminho_base | sed 's|^/||' | sed 's|/|\\\\/|g')
    if grep -q "$raw_url_pattern" "$colecao"; then
        # Busca o método correspondente nas proximidades
        linha_url=$(grep -n "$raw_url_pattern" "$colecao" | head -1 | cut -d':' -f1)
        
        # Busca o método nas 20 linhas anteriores à URL
        inicio=$((linha_url - 20))
        [ $inicio -lt 1 ] && inicio=1
        
        # Extrai as linhas anteriores e verifica se contém o método
        if sed -n "${inicio},${linha_url}p" "$colecao" | grep -i -q "\"method\": \"$metodo\""; then
            return 0  # Endpoint encontrado
        elif sed -n "${inicio},${linha_url}p" "$colecao" | grep -i -q "\"method\": \"$metodo_lower\""; then
            return 0  # Endpoint encontrado (formato minúsculo)
        fi
    fi
    
    return 1  # Endpoint não encontrado
}

# Caminho para o arquivo da coleção completa
COLECAO_COMPLETA="$(dirname "$0")/verdicomply-api-collection-complete.json"

# Verifica se o arquivo existe
if [ ! -f "$COLECAO_COMPLETA" ]; then
    echo -e "${VERMELHO}Erro: Arquivo da coleção não encontrado em $COLECAO_COMPLETA${RESET}"
    exit 1
fi

# Contadores
TOTAL_ENDPOINTS=${#ENDPOINTS[@]}
COBERTOS=0
NAO_COBERTOS=0

# Lista para armazenar endpoints não cobertos
declare -a ENDPOINTS_NAO_COBERTOS

echo -e "${AZUL}Verificando $TOTAL_ENDPOINTS endpoints...${RESET}"
echo ""

# Verifica cada endpoint
for endpoint in "${ENDPOINTS[@]}"; do
    metodo=$(echo $endpoint | cut -d' ' -f1)
    caminho=$(echo $endpoint | cut -d' ' -f2-)
    
    if endpoint_exists "$metodo" "$caminho" "$COLECAO_COMPLETA"; then
        echo -e "${VERDE}✓ $endpoint${RESET}"
        COBERTOS=$((COBERTOS + 1))
    else
        echo -e "${VERMELHO}✗ $endpoint${RESET}"
        ENDPOINTS_NAO_COBERTOS+=("$endpoint")
        NAO_COBERTOS=$((NAO_COBERTOS + 1))
    fi
done

# Exibe resumo
echo ""
echo -e "${AMARELO}=== RESUMO DA VERIFICAÇÃO ===${RESET}"
echo -e "Total de endpoints: $TOTAL_ENDPOINTS"
echo -e "${VERDE}Endpoints cobertos: $COBERTOS${RESET}"
echo -e "${VERMELHO}Endpoints não cobertos: $NAO_COBERTOS${RESET}"

# Exibe lista de endpoints não cobertos, se houver
if [ $NAO_COBERTOS -gt 0 ]; then
    echo ""
    echo -e "${AMARELO}=== ENDPOINTS NÃO COBERTOS ===${RESET}"
    for endpoint in "${ENDPOINTS_NAO_COBERTOS[@]}"; do
        echo -e "${VERMELHO}• $endpoint${RESET}"
    done
    
    echo ""
    echo -e "${AMARELO}Recomendações:${RESET}"
    echo "1. Adicione estes endpoints à coleção Postman"
    echo "2. Execute ./compile_collection.sh para atualizar a coleção completa"
    echo "3. Execute este script novamente para verificar a cobertura"
    
    exit 1
else
    echo ""
    echo -e "${VERDE}✓ Todos os endpoints estão cobertos na coleção Postman!${RESET}"
    exit 0
fi
