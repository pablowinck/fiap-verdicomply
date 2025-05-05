#!/bin/bash
# Script para compilar e executar o utilitário OracleSchemaUtil

# Cores para melhor visualização
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Exibir informações
echo -e "${BLUE}=== UTILITÁRIO DE GERENCIAMENTO DO BANCO ORACLE ===${NC}"
echo -e "${YELLOW}Este utilitário permite limpar o esquema do banco Oracle da FIAP.${NC}"
echo -e "${YELLOW}Útil para resolver problemas com o Flyway e iniciar do zero.${NC}"

# Compilar o projeto
echo -e "\n${BLUE}Compilando o projeto...${NC}"
mvn clean package -DskipTests

# Verificar se compilação foi bem-sucedida
if [ $? -ne 0 ]; then
  echo -e "${RED}Erro ao compilar o projeto. Abortando.${NC}"
  exit 1
fi

# Executar o utilitário
echo -e "\n${GREEN}Executando o utilitário de gerenciamento do banco Oracle...${NC}"
echo -e "${YELLOW}IMPORTANTE: Use a opção 2 para limpar o esquema antes de executar a aplicação!${NC}\n"
java -cp target/verdicomplyapi-0.0.1-SNAPSHOT.jar com.github.pablowinck.verdicomplyapi.utils.DatabaseUtilApplication
