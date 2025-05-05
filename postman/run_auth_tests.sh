#!/bin/bash

# Script para executar apenas os testes de autenticação da API VerdiComply

echo "Executando testes de autenticação da API VerdiComply"
echo "===================================================="

# Verifica se o Newman está instalado
if ! command -v newman &> /dev/null; then
    echo "Newman não está instalado. Instalando..."
    npm install -g newman
fi

# Executa apenas as requisições de autenticação da coleção
echo "Executando testes de autenticação..."
newman run ./auth_requests.json -e ./verdicomply-api-environment.json

echo "Testes de autenticação concluídos!"
