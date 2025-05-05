#!/bin/bash

# Script para compilar todas as partes da coleção em uma única coleção completa

echo "Compilando coleção Postman completa do VerdiComply API"
echo "======================================================"

# Certificando-se que estamos no diretório correto
cd "$(dirname "$0")"

# Combinando todos os arquivos de coleção em um único arquivo
echo "Combinando arquivos de requisição..."
node combine_collection.js

# Renomeando a saída para o formato final
echo "Gerando arquivo final..."
cp verdicomply-api-collection.json verdicomply-api-collection-complete.json

# Adicionando metadados e ajustando configurações
echo "Adicionando metadados..."
jq '.info.name = "VerdiComply API - Coleção Completa" | .info.description = "Coleção completa para testes e documentação da API VerdiComply" | .info.schema = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"' verdicomply-api-collection-complete.json > temp.json
mv temp.json verdicomply-api-collection-complete.json

echo "✅ Coleção compilada com sucesso: verdicomply-api-collection-complete.json"
echo "Execute: ./run_tests.sh para testar toda a coleção"
