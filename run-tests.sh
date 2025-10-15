#!/bin/bash

# Script para executar testes Newman e exibir resultados
# Uso: ./run-tests.sh

echo "=================================================="
echo "  VerdiComply API - Testes Automatizados Newman"
echo "=================================================="
echo ""
echo "Verificando se a aplicação está rodando..."

# Verificar se a aplicação está UP
if curl -s http://localhost:8080/api/public/health > /dev/null 2>&1; then
    echo "✅ Aplicação está rodando em http://localhost:8080"
    echo ""
else
    echo "❌ ERRO: Aplicação não está rodando!"
    echo ""
    echo "Execute primeiro: docker compose up -d"
    echo ""
    exit 1
fi

echo "Executando testes Newman..."
echo ""
echo "=================================================="

# Executar Newman
newman run postman/verdicomply-api-collection-complete.json \
  -e postman/VerdiComply-Dev.postman_environment.json \
  --timeout-request 10000

echo ""
echo "=================================================="
echo ""
echo "📊 Estatísticas Esperadas:"
echo "   - 70 assertions passando (100% de sucesso)"
echo "   - 41 requisições executadas (100% de sucesso)"
echo "   - 0 falhas"
echo "   - Tempo médio: ~24ms"
echo ""
echo "=================================================="
