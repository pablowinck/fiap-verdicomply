#!/bin/bash
# Script para executar comandos SQL no Oracle da FIAP

# Definir cores para melhor visualização
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Credenciais
USERNAME="RM557024"
PASSWORD="240200"
CONNECT_STRING="oracle.fiap.com.br:1521/orcl"

# Função para executar um arquivo SQL
execute_sql_file() {
    local file=$1
    echo -e "${BLUE}Executando arquivo SQL: ${file}${NC}"
    sqlplus -S "${USERNAME}/${PASSWORD}@${CONNECT_STRING}" @"${file}"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Arquivo executado com sucesso!${NC}"
    else
        echo -e "${RED}Erro ao executar o arquivo SQL.${NC}"
        return 1
    fi
}

# Função para executar um comando SQL direto
execute_sql_command() {
    local command=$1
    echo -e "${BLUE}Executando comando SQL...${NC}"
    
    sqlplus -S "${USERNAME}/${PASSWORD}@${CONNECT_STRING}" << EOF
    SET PAGESIZE 100
    SET LINESIZE 150
    ${command}
    EXIT;
EOF
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Comando executado com sucesso!${NC}"
    else
        echo -e "${RED}Erro ao executar o comando SQL.${NC}"
        return 1
    fi
}

# Função para listar as tabelas do usuário
list_tables() {
    echo -e "${BLUE}Listando tabelas do usuário...${NC}"
    
    sqlplus -S "${USERNAME}/${PASSWORD}@${CONNECT_STRING}" << EOF
    SET PAGESIZE 100
    SET LINESIZE 150
    SELECT table_name FROM user_tables ORDER BY table_name;
    EXIT;
EOF
}

# Função para limpar o esquema (remover todas as tabelas e sequências)
clean_schema() {
    echo -e "${BLUE}Limpando esquema do banco de dados...${NC}"
    
    execute_sql_file "/scripts/clean_schema.sql"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Esquema limpo com sucesso!${NC}"
    else
        echo -e "${RED}Erro ao limpar o esquema.${NC}"
        return 1
    fi
}

# Menu principal
show_menu() {
    echo -e "\n${BLUE}=== GERENCIADOR DE BANCO DE DADOS ORACLE FIAP ===${NC}"
    echo "1) Listar tabelas"
    echo "2) Limpar esquema (CUIDADO: remove todas as tabelas e sequências)"
    echo "3) Executar arquivo SQL personalizado"
    echo "4) Executar comando SQL personalizado"
    echo "5) Sair"
    
    read -p "Escolha uma opção: " option
    
    case $option in
        1)
            list_tables
            show_menu
            ;;
        2)
            read -p "Tem certeza que deseja limpar todo o esquema? (s/n): " confirm
            if [[ $confirm =~ ^[Ss]$ ]]; then
                clean_schema
            else
                echo "Operação cancelada."
            fi
            show_menu
            ;;
        3)
            read -p "Digite o nome do arquivo SQL (ex: /scripts/meu_script.sql): " sql_file
            execute_sql_file "$sql_file"
            show_menu
            ;;
        4)
            read -p "Digite o comando SQL: " sql_command
            execute_sql_command "$sql_command"
            show_menu
            ;;
        5)
            echo -e "${GREEN}Saindo...${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}Opção inválida.${NC}"
            show_menu
            ;;
    esac
}

# Verificar se o SQLPlus está disponível
if ! command -v sqlplus &> /dev/null; then
    echo -e "${RED}Erro: SQLPlus não encontrado. Certifique-se de que o cliente Oracle está instalado corretamente.${NC}"
    exit 1
fi

# Iniciar o programa
show_menu
