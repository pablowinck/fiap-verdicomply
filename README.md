# VerdiComply API

## Sobre o Projeto

VerdiComply é uma aplicação RESTful para gerenciamento de auditorias ambientais, permitindo o acompanhamento de conformidades com normas ambientais e o gerenciamento de pendências.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.4.5
- Spring Security (Autenticação JWT + Controle de Acesso Baseado em Papéis)
- Spring Data JPA
- PostgreSQL 16 (migrado do Oracle)
- H2 Database (para testes)
- Flyway para migrações de banco de dados
- Docker e Docker Compose para conteinerização
- GitHub Actions para CI/CD

## Pré-requisitos

- Docker e Docker Compose
- Java 17 (para desenvolvimento local)
- Maven 3.9.9 (para desenvolvimento local)

## Como Executar

### Usando Docker Compose

1. Clone o repositório:

   ```bash
   git clone https://github.com/pablowinck/fiap-verdicomply.git
   cd fiap-verdicomply
   ```

2. Execute a aplicação utilizando Docker Compose:

   ```bash
   docker compose up -d
   ```

   Ou, para limpar o esquema do banco antes da execução:

   ```bash
   ./deploy-with-clean.sh
   ```



### Informações sobre Banco de Dados

A aplicação utiliza **PostgreSQL 16** como banco de dados principal. O Docker Compose gerencia automaticamente a criação e inicialização do banco de dados.

**Configurações padrão (Docker Compose)**:
- **Host**: `postgres` (dentro da rede Docker) ou `localhost` (acesso externo)
- **Porta**: `5433` (mapeada para evitar conflitos com PostgreSQL local)
- **Database**: `verdicomply`
- **Usuário**: `verdicomply`
- **Senha**: `verdicomply`

> **Nota**: Em ambiente de produção, altere as credenciais usando variáveis de ambiente ou serviços de gerenciamento de configuração seguros.

### Configuração do Flyway

O projeto utiliza Flyway para gerenciar migrações de banco de dados PostgreSQL. As migrações estão configuradas para:

- Realizar baseline automático quando necessário (`baseline-on-migrate=true`)
- Executar scripts em ordem correta a partir de `db/migration/postgresql/`
- Criar automaticamente tabelas, índices, constraints e dados iniciais
- Usar convenção de nomenclatura lowercase sem aspas para compatibilidade

### Desenvolvimento Local

1. Clone o repositório:

   ```bash
   git clone https://github.com/pablowinck/fiap-verdicomply.git
   cd fiap-verdicomply
   ```

2. Execute a aplicação:
   ```bash
   mvn spring-boot:run
   ```

## Usuários para Teste

A aplicação inclui três usuários pré-configurados para testes:

- **Auditor**:

  - Usuário: `auditor`
  - Senha: `auditor123`
  - Papel: `ROLE_AUDITOR`

- **Gestor**:

  - Usuário: `gestor`
  - Senha: `gestor123`
  - Papéis: `ROLE_GESTOR`, `ROLE_AUDITOR`

- **Administrador**:
  - Usuário: `admin`
  - Senha: `admin123`
  - Papéis: `ROLE_ADMIN`, `ROLE_GESTOR`, `ROLE_AUDITOR`

## Estrutura da API

A API é dividida em quatro principais grupos de endpoints:

1. **Auditorias**: `/api/auditorias/**`

   - Listar todas as auditorias
   - Buscar auditoria por ID
   - Criar nova auditoria
   - Atualizar auditoria existente
   - Excluir auditoria
   - Buscar por status
   - Buscar por departamento

2. **Conformidades**: `/api/conformidades/**`

   - Listar todas as conformidades
   - Buscar conformidade por ID
   - Criar nova conformidade
   - Atualizar conformidade existente
   - Excluir conformidade
   - Buscar por auditoria
   - Buscar por status
   - Buscar por norma

3. **Pendências**: `/api/pendencias/**`

   - Listar todas as pendências
   - Buscar pendência por ID
   - Criar nova pendência
   - Atualizar pendência existente
   - Excluir pendência
   - Buscar por conformidade
   - Buscar por status
   - Buscar pendências vencidas

4. **Logs**: `/api/logs/**`
   - Listar todos os logs
   - Buscar log por ID
   - Criar novo log
   - Excluir log
   - Buscar por conformidade
   - Buscar por ação

## Segurança

Todos os endpoints estão protegidos e requerem autenticação, com exceção de `/api/public/**`.

### Autenticação JWT

A API utiliza JWT (JSON Web Token) para autenticação segura de usuários.

1. **Obter Token**:

   - Endpoint: `POST /api/public/auth/login`
   - Corpo da requisição: `{"username": "seu-usuario", "password": "sua-senha"}`
   - Resposta: `{"tipo": "Bearer", "token": "seu-token-jwt", "username": "seu-usuario", "roles": ["ROLE_X", "ROLE_Y"]}`

2. **Autenticar Requisições**:

   - Adicione o header `Authorization: Bearer seu-token-jwt` em todas as requisições para endpoints protegidos

3. **Tempo de Expiração**:
   - Os tokens JWT expiram após 24 horas (86400000 milissegundos)

### Níveis de Acesso

- **AUDITOR**: Podem visualizar auditorias, conformidades, pendências e logs.
- **GESTOR**: Podem visualizar e gerenciar auditorias, conformidades, pendências e logs.
- **ADMIN**: Têm acesso completo a todas as operações, incluindo exclusão de recursos.

### Exemplos de Chamadas

Exemplos detalhados de como fazer chamadas autenticadas para a API estão disponíveis no arquivo `exemplos-curl.md`.

## Coleção Postman

Para facilitar o teste e a documentação da API, disponibilizamos uma coleção completa do Postman com todos os endpoints disponíveis.

### Como Utilizar

**Importante:** Após subir a aplicação com Docker Compose, recomendamos fortemente importar a collection e as variáveis de ambiente no Postman para testar adequadamente todos os endpoints:

1. Importe a coleção do diretório `postman/verdicomply-api-collection-complete.json` para o Postman
2. Importe as variáveis de ambiente do diretório `postman/verdicomply-api-environment.json`
3. Utilize a collection para testar todos os endpoints com as credenciais pré-configuradas
3. Utilize o endpoint "Autenticar" na pasta "Autenticação" para obter o token JWT
4. O token será automaticamente configurado para todas as demais requisições da coleção

### Recursos Incluídos

- **Autenticação**: Login e registro de usuários
- **Auditorias**: Gerenciamento completo de auditorias ambientais
- **Normas Ambientais**: Consulta e gerenciamento de normas ambientais
- **Conformidades**: Registro e acompanhamento de conformidades
- **Pendências**: Gerenciamento de ações pendentes
- **Logs**: Registros de mudanças em conformidades

### Preparando o Ambiente de Teste

Antes de executar os testes, prepare o ambiente de teste com dados iniciais:

```bash
# Inicie a API primeiro
./mvnw spring-boot:run

# Em outro terminal, prepare o ambiente de teste
./postman/preparar_ambiente_teste.sh
```

Este script cria recursos de teste (norma ambiental, auditoria, conformidade, pendência e log) e atualiza o arquivo de variáveis de ambiente do Postman com os IDs gerados.

### Execução Automatizada

A coleção inclui scripts de pré-requisição e testes para facilitar a execução automatizada:

```bash
# Executar todos os testes via Newman (CLI do Postman)
./postman/run_tests.sh

# Executar apenas endpoints específicos
./postman/run_auth_tests.sh
```

Para mais detalhes sobre a decisão de arquitetura relacionada à coleção Postman, consulte o documento [ADR_002_Postman_Collection.md](docs/ADR_002_Postman_Collection.md).

### Resultados dos Testes Automatizados

A aplicação possui **100% de taxa de sucesso** nos testes automatizados via Newman:

**Estatísticas dos Testes:**
- ✅ **70 assertions passando** (100% de sucesso)
- ✅ **41 requisições executadas** (100% de sucesso)
- ✅ **0 falhas**
- ✅ **Tempo médio de resposta**: 24ms
- ✅ **Tempo total de execução**: ~1.6s

**Executar testes localmente:**
```bash
# Garantir que a aplicação está rodando
docker compose up -d

# Executar testes Newman
newman run postman/verdicomply-api-collection-complete.json \
  -e postman/VerdiComply-Dev.postman_environment.json
```

---

## DevOps e CI/CD

### Containerização

A aplicação está containerizada usando Docker com uma estratégia multi-stage build para otimizar o tamanho da imagem final.

#### Dockerfile

- **Stage 1 (Build)**: Utiliza `maven:3.9-eclipse-temurin-17` para compilar a aplicação
- **Stage 2 (Runtime)**: Utiliza `eclipse-temurin:17-jre` (imagem mais leve) para executar a aplicação
- **Otimizações**:
  - Cache de dependências Maven
  - Usuário não-root para segurança
  - Configuração de JVM otimizada para containers

#### Docker Compose

O `docker-compose.yml` inclui:

- **Serviço da Aplicação**: Spring Boot API na porta 8080
- **PostgreSQL**: Banco de dados na porta 5433 (para evitar conflitos locais)
- **Volumes**: Persistência de dados do PostgreSQL
- **Rede**: Rede isolada `verdicomply-network`
- **Health Checks**: Garantem que o PostgreSQL está pronto antes de iniciar a aplicação

**Como executar com Docker Compose:**

```bash
# Subir todos os serviços
docker compose up -d

# Ver logs
docker compose logs -f

# Parar serviços
docker compose down

# Parar e remover volumes (limpar dados)
docker compose down -v
```

### Pipeline CI/CD

O projeto implementa um pipeline completo de CI/CD usando GitHub Actions (`.github/workflows/ci-cd.yml`).

#### Etapas do Pipeline

**1. Build and Test**
- Checkout do código
- Setup do JDK 17 com cache do Maven
- Build da aplicação
- Execução de testes unitários
- Execução de testes de integração
- Geração de relatórios de cobertura
- Upload de artefatos (JAR)

**2. Docker Build and Push**
- Configuração do Docker Buildx
- Login no Docker Hub
- Build da imagem Docker
- Push para Docker Hub com tags:
  - `latest` (branch main)
  - `develop` (branch develop)
  - `<branch>-<sha>` (hash do commit)
- Cache de layers para builds mais rápidos

**3. Deploy to Staging** (branch develop)
- Deploy automático para ambiente de staging
- Execução de smoke tests
- URL: `https://staging.verdicomply.com`

**4. Deploy to Production** (branch main)
- Deploy para produção
- Execução de smoke tests
- Notificação de deploy bem-sucedido
- URL: `https://verdicomply.com`

#### Configuração de Secrets

Para que o pipeline funcione, configure os seguintes secrets no GitHub:

- `DOCKER_USERNAME`: Seu usuário do Docker Hub
- `DOCKER_PASSWORD`: Sua senha ou token do Docker Hub

**Como configurar:**
1. Vá em Settings > Secrets and variables > Actions
2. Adicione os secrets necessários

#### Testes Automatizados

O pipeline executa:

- **Testes Unit ários** com profile `unit-tests`
- **Testes de Integração** com profile `integration-tests` usando H2 in-memory
- **Testes via Newman**: As collections Postman podem ser executadas automaticamente

### Migração Oracle → PostgreSQL

A aplicação foi migrada de Oracle para PostgreSQL incluindo:

1. **Dependências**:
   - Removido `ojdbc11` e `flyway-database-oracle`
   - Adicionado `postgresql` e `flyway-database-postgresql`

2. **Migrações SQL**:
   - Convertidas de sintaxe Oracle para PostgreSQL
   - Criadas em `db/migration/postgresql/`
   - Utilizadas aspas duplas para preservar case-sensitivity dos nomes

3. **Testes**:
   - H2 em modo PostgreSQL para testes unitários e de integração
   - Flyway desabilitado em testes (usa schema.sql e data.sql)

### Ambientes

- **Local**: Docker Compose com PostgreSQL local
- **Teste**: H2 in-memory (PostgreSQL mode)
- **Staging**: Deploy automático via GitHub Actions (branch develop)
- **Production**: Deploy automático via GitHub Actions (branch main)

### Tecnologias DevOps

- **Containerização**: Docker, Docker Compose
- **CI/CD**: GitHub Actions
- **Banco de Dados**: PostgreSQL 16
- **Build**: Maven 3.9.9
- **Testes**: JUnit 5, RestAssured, Newman (Postman CLI)
- **Registry**: Docker Hub

Para mais detalhes técnicos, consulte `DOCUMENTACAO.md`.
