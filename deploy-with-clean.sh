#!/bin/bash
# Script para limpar o esquema, compilar e implantar a aplicação

# Cores para visualização
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Função para exibir mensagens de status
status() {
  echo -e "${BLUE}===> $1${NC}"
}

error() {
  echo -e "${RED}ERRO: $1${NC}"
  exit 1
}

warning() {
  echo -e "${YELLOW}AVISO: $1${NC}"
}

success() {
  echo -e "${GREEN}SUCESSO: $1${NC}"
}

# Verificar se o Maven está instalado
if ! command -v mvn &> /dev/null; then
  error "Maven não encontrado. Por favor, instale o Maven primeiro."
fi

# Verificar se o Docker está instalado
if ! command -v docker &> /dev/null; then
  error "Docker não encontrado. Por favor, instale o Docker primeiro."
fi

# Etapa 1: Compilar o utilitário de limpeza do esquema Oracle
status "Compilando o projeto com o utilitário de limpeza..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
  error "Falha ao compilar o projeto."
fi
success "Projeto compilado com sucesso!"

# Etapa 2: Perguntar ao usuário se deseja limpar o esquema Oracle da FIAP
read -p "Deseja limpar o esquema Oracle da FIAP antes da implantação? (s/n): " clean_schema
if [[ $clean_schema =~ ^[Ss]$ ]]; then
  status "Executando utilitário de limpeza de esquema..."
  
  # Executar o novo utilitário Oracle independente para limpar o esquema
  ./oracle-util.sh clean
  
  if [ $? -ne 0 ]; then
    error "Falha ao limpar o esquema Oracle da FIAP."
  fi
  success "Esquema limpo com sucesso!"
else
  warning "Pulando a limpeza do esquema."
fi

# Etapa 3: Iniciar os contêineres Docker
status "Iniciando os contêineres Docker..."
docker compose down
docker compose up -d

if [ $? -ne 0 ]; then
  error "Falha ao iniciar os contêineres Docker."
fi
success "Contêineres iniciados com sucesso!"

# Etapa 4: Verificar o status da aplicação
status "Verificando o status da aplicação..."
sleep 5 # Aguardar inicialização

# Exibir logs iniciais
docker logs verdicomply-api --tail 20

# Perguntar se deseja acompanhar os logs
read -p "Deseja acompanhar os logs da aplicação? (s/n): " follow_logs
if [[ $follow_logs =~ ^[Ss]$ ]]; then
  docker logs verdicomply-api -f
fi

success "Processo de implantação concluído!"
echo -e "${GREEN}==========================================${NC}"
echo -e "${GREEN}A aplicação está rodando em: http://localhost:8080${NC}"
echo -e "${GREEN}==========================================${NC}"
