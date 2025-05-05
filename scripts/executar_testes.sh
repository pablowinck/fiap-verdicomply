#!/bin/bash

echo "=== EXECUTANDO TESTES UNITÁRIOS E INTEGRADOS ==="

# Cores para formatação
VERDE='\033[0;32m'
VERMELHO='\033[0;31m'
AMARELO='\033[0;33m'
RESET='\033[0m'

# Diretório do projeto
DIR_PROJETO="/Users/pablowinter/projects/pessoal/verdicomplyapi"

# Função para executar testes e reportar resultados
executar_testes() {
    local perfil=$1
    local descricao=$2
    
    echo -e "\n${AMARELO}=== EXECUTANDO $descricao ===${RESET}"
    
    cd "$DIR_PROJETO"
    
    # Limpa antes de executar para garantir que tudo seja recompilado
    echo "Executando clean..."
    mvn clean -q
    
    # Executa os testes com o perfil especificado
    echo "Executando testes com perfil $perfil..."
    if mvn test -P$perfil; then
        echo -e "${VERDE}✓ $descricao concluídos com sucesso!${RESET}"
        return 0
    else
        echo -e "${VERMELHO}✗ Falha ao executar $descricao.${RESET}"
        return 1
    fi
}

# Função para resumir endpoints disponíveis
listar_endpoints() {
    echo -e "\n${AMARELO}=== ENDPOINTS DISPONÍVEIS NA API ===${RESET}"
    echo -e "${VERDE}Autenticação:${RESET}"
    echo " - POST /api/public/auth/login"
    echo " - POST /api/public/registro"
    
    echo -e "\n${VERDE}Health Check:${RESET}"
    echo " - GET /api/public/health"
    
    echo -e "\n${VERDE}Normas Ambientais:${RESET}"
    echo " - GET /api/normas"
    echo " - GET /api/normas/{id}"
    echo " - GET /api/normas/orgao/{orgaoFiscalizador}"
    echo " - GET /api/normas/codigo/{codigoNorma}"
    echo " - POST /api/normas"
    echo " - PUT /api/normas/{id}"
    echo " - DELETE /api/normas/{id}"
    
    echo -e "\n${VERDE}Auditorias:${RESET}"
    echo " - GET /api/auditorias"
    echo " - GET /api/auditorias/{id}"
    echo " - GET /api/auditorias/status/{status}"
    echo " - GET /api/auditorias/departamento/{departamentoId}"
    echo " - POST /api/auditorias"
    echo " - PUT /api/auditorias/{id}"
    echo " - DELETE /api/auditorias/{id}"
    
    echo -e "\n${VERDE}Conformidades:${RESET}"
    echo " - GET /api/conformidades"
    echo " - GET /api/conformidades/{id}"
    echo " - GET /api/conformidades/auditoria/{auditoriaId}"
    echo " - GET /api/conformidades/status/{estaConforme}"
    echo " - GET /api/conformidades/norma/{normaAmbientalId}"
    echo " - POST /api/conformidades"
    echo " - PUT /api/conformidades/{id}"
    echo " - DELETE /api/conformidades/{id}"
    
    echo -e "\n${VERDE}Pendências:${RESET}"
    echo " - GET /api/pendencias"
    echo " - GET /api/pendencias/{id}"
    echo " - GET /api/pendencias/conformidade/{conformidadeId}"
    echo " - GET /api/pendencias/status/{resolvida}"
    echo " - GET /api/pendencias/vencidas"
    echo " - POST /api/pendencias"
    echo " - PUT /api/pendencias/{id}"
    echo " - DELETE /api/pendencias/{id}"
    
    echo -e "\n${VERDE}Logs de Conformidade:${RESET}"
    echo " - GET /api/logs"
    echo " - GET /api/logs/{id}"
    echo " - GET /api/logs/conformidade/{conformidadeId}"
    echo " - GET /api/logs/acao/{acao}"
    echo " - POST /api/logs"
    echo " - DELETE /api/logs/{id}"
    
    echo -e "\n${VERDE}Debug (desenvolvimento):${RESET}"
    echo " - GET /api/debug/normas"
    echo " - GET /api/debug/normas/count"
    echo " - GET /api/debug/normas/schema"
    echo " - GET /api/debug/normas/teste-conversao"
    echo " - POST /api/debug/normas/create-jpa"
    echo " - POST /api/debug/normas/create-sql"
    
    echo -e "\n${VERDE}Fix (desenvolvimento):${RESET}"
    echo " - GET /api/fix/normas/schema"
    echo " - POST /api/fix/normas/fix-schema"
    echo " - POST /api/fix/normas/recriar-normas"
}

# Executa testes unitários
executar_testes "unit-tests" "TESTES UNITÁRIOS"
RESULTADO_UNITARIOS=$?

# Executa testes integrados
executar_testes "integration-tests" "TESTES INTEGRADOS"
RESULTADO_INTEGRADOS=$?

# Lista endpoints disponíveis
listar_endpoints

# Exibe resumo final
echo -e "\n${AMARELO}=== RESUMO DA EXECUÇÃO ===${RESET}"

if [ $RESULTADO_UNITARIOS -eq 0 ]; then
    echo -e "${VERDE}✓ Testes unitários: OK${RESET}"
else
    echo -e "${VERMELHO}✗ Testes unitários: FALHA${RESET}"
fi

if [ $RESULTADO_INTEGRADOS -eq 0 ]; then
    echo -e "${VERDE}✓ Testes integrados: OK${RESET}"
else
    echo -e "${VERMELHO}✗ Testes integrados: FALHA${RESET}"
fi

echo -e "\n${AMARELO}=== VERIFICAÇÃO DE ENDPOINTS ===${RESET}"
echo -e "Todos os endpoints da API estão listados acima e devem estar contemplados na coleção Postman."
echo -e "Para testar os endpoints manualmente, execute ./postman/run_final_tests.sh"

echo -e "\n${AMARELO}=== CONCLUÍDO ===${RESET}"
