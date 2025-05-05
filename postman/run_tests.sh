#!/bin/bash

# Script para executar todos os testes da coleção Postman via Newman

echo "Executando testes da API VerdiComply com Newman"
echo "==============================================="

# Verifica se o Newman está instalado
if ! command -v newman &> /dev/null; then
    echo "Newman não está instalado. Instalando..."
    npm install -g newman
fi

# Executa a coleção completa com o ambiente de desenvolvimento
echo "Executando todos os testes da API..."
newman run ./verdicomply-api-collection-complete.json -e ./verdicomply-api-environment.json

echo "Testes concluídos!"
