#!/bin/bash
set -e

function log() {
  echo "$(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log "Verificando status do banco Oracle..."

# Tenta se conectar ao serviço Oracle com sqlplus
# Observe que não expomos senhas no script diretamente
docker exec ecotrack-oracle bash -c "echo 'exit' | sqlplus -s verdicomply/verdicomply@//localhost:1521/XEPDB1" > /dev/null 2>&1

if [ $? -ne 0 ]; then
  log "Banco Oracle ainda não está pronto para conexões."
  exit 1
else
  log "Banco Oracle está pronto para conexões!"
  exit 0
fi
