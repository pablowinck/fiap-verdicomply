#!/bin/bash

# Definir cores para melhor legibilidade
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Banner
echo -e "${GREEN}=== UTILITÁRIO DE GERENCIAMENTO DO BANCO ORACLE ===${NC}"
echo "Este utilitário permite limpar o esquema do banco Oracle da FIAP."
echo "Útil para resolver problemas com o Flyway e iniciar do zero."

# Mudar para o diretório do utilitário
cd scripts/oracle-util

# Compilar o utilitário se o JAR não existir ou se for solicitada recompilação
if [ ! -f "target/oracle-util-1.0-SNAPSHOT-jar-with-dependencies.jar" ] || [ "$1" == "rebuild" ]; then
    echo -e "\n${GREEN}Compilando o utilitário...${NC}"
    mvn clean package -q
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}Erro ao compilar o utilitário. Abortando.${NC}"
        exit 1
    fi
    
    # Remover o argumento rebuild se presente
    if [ "$1" == "rebuild" ]; then
        shift
    fi
fi

# Executar o utilitário com os argumentos passados
echo -e "\n${GREEN}Executando o utilitário de gerenciamento do banco Oracle...${NC}"

# Executar o utilitário com todos os argumentos passados
java -jar target/oracle-util-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"

# Voltar ao diretório original
cd ../..
