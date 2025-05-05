#!/bin/bash

# Cores para melhor visualização
VERDE='\033[0;32m'
VERMELHO='\033[0;31m'
AMARELO='\033[0;33m'
AZUL='\033[0;34m'
RESET='\033[0m'

# Diretório base (onde está sendo executado o script)
DIR=$(pwd)

# Arquivos de collection e ambiente
COLLECTION="$DIR/postman/verdicomply-api-collection-complete.json"
AMBIENTE="$DIR/postman/verdicomply-api-environment.json"
TEMP_AMBIENTE="$DIR/postman/temp_environment.json"

# Função para exibir mensagem de separação
separador() {
    echo -e "${AZUL}================================================================================${RESET}"
    echo -e "${AZUL}$1${RESET}"
    echo -e "${AZUL}================================================================================${RESET}"
    echo ""
}

# Verificar se Newman está instalado
if ! command -v newman &> /dev/null; then
    echo -e "${VERMELHO}Newman não está instalado. Por favor instale com: npm install -g newman${RESET}"
    exit 1
fi

# Iniciar os testes sequenciais
separador "🚀 INICIANDO TESTES SEQUENCIAIS DA API VERDICOMPLY"

# 1. Testar endpoint de health check (não requer autenticação)
separador "1️⃣ TESTANDO HEALTH CHECK"
newman run "$COLLECTION" -e "$AMBIENTE" --folder "Health Check"

# 2. Fazer login para obter token JWT e atualizar ambiente
separador "2️⃣ REALIZANDO LOGIN PARA OBTER TOKEN JWT"
RESULTADO=$(newman run "$COLLECTION" -e "$AMBIENTE" --folder "Autenticação e Registro" --bail --reporter-cli-no-assertions --reporter-cli-no-console --reporter-json-export="$DIR/postman/login_result.json")

if [ $? -ne 0 ]; then
    echo -e "${VERMELHO}❌ Falha ao obter token JWT. Verificar detalhes acima.${RESET}"
    exit 1
fi

echo -e "${VERDE}✅ Login realizado com sucesso. Token JWT obtido.${RESET}"

# 3. Testar endpoints de auditoria (requer autenticação)
separador "3️⃣ TESTANDO ENDPOINTS DE AUDITORIA"
newman run "$COLLECTION" -e "$AMBIENTE" --folder "Auditorias"

# 4. Testar endpoints de conformidade (requer autenticação)
separador "4️⃣ TESTANDO ENDPOINTS DE CONFORMIDADE"
newman run "$COLLECTION" -e "$AMBIENTE" --folder "Conformidades"

# 5. Testar endpoints de pendências (requer autenticação)
separador "5️⃣ TESTANDO ENDPOINTS DE PENDÊNCIAS"
newman run "$COLLECTION" -e "$AMBIENTE" --folder "Pendências"

# 6. Testar endpoints de logs de conformidade (requer autenticação)
separador "6️⃣ TESTANDO ENDPOINTS DE LOGS DE CONFORMIDADE"
newman run "$COLLECTION" -e "$AMBIENTE" --folder "Logs de Conformidade"

# Sumário final
separador "🏁 TESTES CONCLUÍDOS"
echo -e "${VERDE}Os testes foram executados sequencialmente.${RESET}"
echo -e "${AMARELO}Observe as saídas acima para verificar sucessos e falhas.${RESET}"
