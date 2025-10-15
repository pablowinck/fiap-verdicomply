# Documentação Técnica - VerdiComply API DevOps

## Integrantes do Projeto
- Nome: [Seu Nome]
- RM: [Seu RM]

## Índice
1. [Introdução](#introdução)
2. [Arquitetura](#arquitetura)
3. [Pipeline CI/CD](#pipeline-cicd)
4. [Containerização](#containerização)
5. [Comandos Docker](#comandos-docker)
6. [Migração de Banco de Dados](#migração-de-banco-de-dados)
7. [Desafios e Soluções](#desafios-e-soluções)
8. [Testes](#testes)

---

## Introdução

Este documento descreve a implementação completa de DevOps para a aplicação VerdiComply API, incluindo containerização com Docker, pipeline CI/CD com GitHub Actions e migração de Oracle para PostgreSQL.

### Objetivo

Implementar práticas de DevOps modernas para automatizar o ciclo de vida da aplicação, desde o desenvolvimento até a produção, garantindo:
- Build automatizado
- Testes contínuos
- Deploy automatizado
- Infraestrutura como código
- Containerização eficiente

---

## Arquitetura

### Arquitetura de Aplicação

```
┌─────────────────────────────────────────┐
│         GitHub Repository               │
│  (Código Fonte + Configurações)         │
└─────────────────┬───────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────┐
│         GitHub Actions                  │
│  ┌───────────────────────────────────┐  │
│  │  1. Build & Test                  │  │
│  │  2. Docker Build & Push           │  │
│  │  3. Deploy Staging                │  │
│  │  4. Deploy Production             │  │
│  └───────────────────────────────────┘  │
└─────────────────┬───────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────┐
│         Docker Hub                      │
│  (Imagens Docker Versionadas)           │
└─────────────────┬───────────────────────┘
                  │
        ┌─────────┴─────────┐
        │                   │
        ↓                   ↓
┌──────────────┐    ┌──────────────┐
│   Staging    │    │  Production  │
│              │    │              │
│  Docker      │    │  Docker      │
│  Compose     │    │  Compose     │
│              │    │              │
│  - API       │    │  - API       │
│  - PostgreSQL│    │  - PostgreSQL│
└──────────────┘    └──────────────┘
```

### Stack Tecnológica

- **Backend**: Java 17 + Spring Boot 3.4.5
- **Banco de Dados**: PostgreSQL 16
- **Testes**: H2 Database (in-memory)
- **Build**: Maven 3.9.9
- **Containerização**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **Registry**: Docker Hub

---

## Pipeline CI/CD

### Descrição do Pipeline

O pipeline implementado no GitHub Actions (`.github/workflows/ci-cd.yml`) possui quatro jobs principais:

#### 1. Build and Test

**Trigger**: Push em `main` ou `develop` | Pull Request para `main`

**Steps**:
```yaml
- Checkout do código
- Setup JDK 17 (Temurin)
- Cache de dependências Maven
- Build: mvn clean package -DskipTests
- Testes Unitários: mvn test -P unit-tests
- Testes de Integração: mvn verify -P integration-tests
- Relatório de Cobertura: mvn jacoco:report
- Upload de artefatos (JAR)
```

**Tempo Estimado**: 3-5 minutos

#### 2. Docker Build and Push

**Trigger**: Push (após build bem-sucedido)

**Steps**:
```yaml
- Setup Docker Buildx
- Login Docker Hub
- Extração de metadata (tags)
- Build da imagem
- Push para Docker Hub
```

**Tags Geradas**:
- `latest` (branch main)
- `develop` (branch develop)
- `main-<sha>` ou `develop-<sha>`

**Otimizações**:
- Cache de layers do Docker
- Multi-stage build
- Build paralelizado

**Tempo Estimado**: 2-4 minutos

#### 3. Deploy to Staging

**Trigger**: Push na branch `develop`

**Environment**: staging
**URL**: https://staging.verdicomply.com

**Steps**:
```yaml
- Deploy da imagem para servidor staging
- Execução de smoke tests
```

**Tempo Estimado**: 1-2 minutos

#### 4. Deploy to Production

**Trigger**: Push na branch `main`

**Environment**: production
**URL**: https://verdicomply.com

**Steps**:
```yaml
- Deploy da imagem para servidor production
- Execução de smoke tests
- Notificação de sucesso
```

**Tempo Estimado**: 1-2 minutos

### Diagrama do Fluxo

```
┌──────────────┐
│  git push    │
│  (main/dev)  │
└──────┬───────┘
       │
       ↓
┌──────────────────┐
│ Build & Test     │ ← Maven build, testes unitários/integração
│ ✓ Compile        │
│ ✓ Unit Tests     │
│ ✓ Integration    │
└──────┬───────────┘
       │ Success
       ↓
┌──────────────────┐
│ Docker Build     │ ← Multi-stage build
│ ✓ Build Image    │
│ ✓ Tag & Push     │
└──────┬───────────┘
       │
       ├─────── develop ────→ Deploy Staging
       │
       └─────── main ───────→ Deploy Production
```

---

## Containerização

### Dockerfile

**Estratégia**: Multi-stage build

#### Stage 1: Build
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B
```

**Benefícios**:
- Cache de dependências Maven
- Build isolado do runtime
- Redução do tamanho final da imagem

#### Stage 2: Runtime
```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring
COPY --from=build /app/target/*.jar app.jar
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**Benefícios**:
- Imagem final leve (apenas JRE)
- Segurança (usuário não-root)
- Configuração otimizada de memória

**Tamanho das Imagens**:
- Build stage: ~650 MB
- Runtime final: ~280 MB

### Docker Compose

#### Serviços

**1. PostgreSQL**
```yaml
postgres:
  image: postgres:16-alpine
  ports:
    - "5433:5432"
  environment:
    POSTGRES_DB: verdicomply
    POSTGRES_USER: verdicomply
    POSTGRES_PASSWORD: verdicomply
  volumes:
    - postgres_data:/var/lib/postgresql/data
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U verdicomply"]
    interval: 10s
    timeout: 5s
    retries: 5
```

**2. Aplicação**
```yaml
app:
  build: .
  ports:
    - "8080:8080"
  depends_on:
    postgres:
      condition: service_healthy
  environment:
    DB_HOST: postgres
    DB_PORT: 5432
    DB_NAME: verdicomply
    DB_USER: verdicomply
    DB_PASSWORD: verdicomply
```

**Rede e Volumes**:
```yaml
networks:
  verdicomply-network:
    driver: bridge

volumes:
  postgres_data:
    driver: local
```

---

## Comandos Docker

### Desenvolvimento Local

```bash
# Build da imagem
docker compose build

# Subir todos os serviços
docker compose up -d

# Ver logs em tempo real
docker compose logs -f

# Ver logs de um serviço específico
docker compose logs -f app

# Parar serviços
docker compose down

# Parar e limpar volumes (remover dados)
docker compose down -v

# Rebuild sem cache
docker compose build --no-cache

# Executar comandos dentro do container
docker compose exec app sh
docker compose exec postgres psql -U verdicomply -d verdicomply
```

### Gestão de Imagens

```bash
# Listar imagens locais
docker images

# Remover imagens não utilizadas
docker image prune -a

# Ver tamanho das layers
docker history verdicomplyapi-app

# Inspecionar imagem
docker inspect verdicomplyapi-app
```

### Debug

```bash
# Ver status dos containers
docker compose ps

# Ver recursos consumidos
docker stats

# Inspecionar rede
docker network inspect verdicomplyapi_verdicomply-network

# Inspecionar volume
docker volume inspect verdicomplyapi_postgres_data
```

---

## Migração de Banco de Dados

### Oracle → PostgreSQL

#### Mudanças Necessárias

**1. Dependências (pom.xml)**

Removido:
```xml
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc11</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-oracle</artifactId>
</dependency>
```

Adicionado:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

**2. Configuração (application.properties)**

Antes (Oracle):
```properties
spring.datasource.url=jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
```

Depois (PostgreSQL):
```properties
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:verdicomply}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.flyway.locations=classpath:db/migration/postgresql
```

**3. Migrações SQL**

Oracle → PostgreSQL:
- `NUMBER` → `SERIAL` ou `INTEGER`
- `VARCHAR2` → `VARCHAR`
- `DATE` → `TIMESTAMP`
- `SYSDATE` → `CURRENT_TIMESTAMP`
- `SEQUENCE.NEXTVAL` → Auto-incremento com `SERIAL`
- Aspas duplas para preservar case-sensitivity

Exemplo de migração V1:
```sql
-- Oracle
CREATE TABLE NORMA_AMBIENTAL (
    ID_NORMA NUMBER PRIMARY KEY,
    CODIGO_NORMA VARCHAR2(20) NOT NULL
);

-- PostgreSQL
CREATE TABLE "NORMA_AMBIENTAL" (
    "ID_NORMA" SERIAL PRIMARY KEY,
    "CODIGO_NORMA" VARCHAR(20) NOT NULL
);
```

**4. Triggers e Functions**

Oracle PL/SQL → PostgreSQL PL/pgSQL:
- `CREATE OR REPLACE TRIGGER` similar mas com sintaxe diferente
- Funções agora retornam `TRIGGER`
- Use `LANGUAGE plpgsql`

---

## Desafios e Soluções

### 1. Case-Sensitivity no PostgreSQL

**Problema**: PostgreSQL converte nomes de colunas/tabelas para minúsculas por padrão, causando incompatibilidade com entidades JPA que usam nomes em maiúsculas.

**Erro**:
```
ERROR: column na1_0.severidade does not exist
```

**Solução**: Adicionar aspas duplas em todos os identificadores nas migrações SQL:
```sql
CREATE TABLE "NORMA_AMBIENTAL" (
    "ID_NORMA" SERIAL PRIMARY KEY,
    "SEVERIDADE" VARCHAR(20)
);
```

### 2. Utilitários Oracle

**Problema**: Código de utilitários Oracle (DatabaseUtilApplication) tentando carregar driver Oracle inexistente.

**Solução**: Remover arquivos de utilitários Oracle específicos:
```bash
rm src/main/java/.../ utils/DatabaseUtilApplication.java
rm src/main/java/.../utils/OracleSchemaUtil.java
```

### 3. Porta do PostgreSQL em Uso

**Problema**: Porta 5432 já ocupada por PostgreSQL local.

**Erro**:
```
Bind for 0.0.0.0:5432 failed: port is already allocated
```

**Solução**: Mapear para porta diferente no docker-compose.yml:
```yaml
ports:
  - "5433:5432"
```

### 4. Imagens Docker Não Disponíveis

**Problema**: Imagem `maven:3.9.9-eclipse-temurin-17-alpine` não existe para a plataforma.

**Solução**: Usar versão sem `-alpine`:
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
FROM eclipse-temurin:17-jre
```

---

## Testes

### Estrutura de Testes

**Testes Unitários** (`*Test.java`):
- Profile: `unit-tests`
- Banco: H2 in-memory
- Comando: `mvn test -P unit-tests`

**Testes de Integração** (`*IT.java`):
- Profile: `integration-tests`
- Banco: H2 in-memory (modo PostgreSQL)
- Comando: `mvn verify -P integration-tests`

### Configuração H2 para Testes

```properties
spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.flyway.enabled=false
```

### Testes com Newman (Postman CLI)

```bash
# Instalar Newman
npm install -g newman

# Executar collection
newman run postman/verdicomply-api-collection-complete.json \
  --environment postman/verdicomply-api-environment.json

# Com relatórios
newman run postman/verdicomply-api-collection-complete.json \
  --environment postman/verdicomply-api-environment.json \
  --reporters cli,html \
  --reporter-html-export newman-report.html
```

---

## Checklist de Entrega

- [x] Migração de Oracle para PostgreSQL
- [x] Configuração H2 para testes
- [x] Dockerfile otimizado (multi-stage)
- [x] docker-compose.yml completo
- [x] Pipeline CI/CD GitHub Actions
- [x] Build automatizado
- [x] Testes automatizados
- [x] Docker build e push automatizado
- [x] Deploy staging/production
- [x] README.md atualizado
- [x] DOCUMENTACAO.md criada
- [x] Estrutura do projeto organizada
- [ ] Prints do pipeline funcionando
- [ ] Prints dos ambientes rodando

---

## Próximos Passos

1. **Capturar Screenshots**:
   - Pipeline GitHub Actions executando
   - Docker containers rodando (`docker compose ps`)
   - Aplicação funcionando (endpoint de health)

2. **Melhorias Futuras**:
   - Implementar health checks na aplicação Spring Boot
   - Adicionar métricas com Prometheus/Grafana
   - Configurar deploy real em cloud (AWS, Azure, GCP)
   - Implementar rollback automático em caso de falha
   - Adicionar testes de performance
   - Implementar monitoramento com ELK Stack

3. **Segurança**:
   - Implementar scan de vulnerabilidades nas imagens Docker
   - Adicionar análise de código estático (SonarQube)
   - Configurar secrets management adequado
   - Implementar HTTPS/TLS nos ambientes

---

**Documentação gerada em**: 2025-10-14
**Versão**: 1.0.0
