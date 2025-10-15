# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Sobre o Projeto

VerdiComply é uma API RESTful desenvolvida em Spring Boot para gerenciamento de auditorias ambientais, incluindo conformidades com normas ambientais e o gerenciamento de pendências. A aplicação utiliza PostgreSQL 16 como banco de dados e implementa autenticação JWT com controle de acesso baseado em papéis (RBAC).

## Stack Tecnológica

- **Framework**: Spring Boot 3.4.5
- **Java**: 17
- **Build Tool**: Maven 3.9.9
- **Database**: PostgreSQL 16
- **Migrações**: Flyway
- **Segurança**: Spring Security com JWT
- **Containerização**: Docker e Docker Compose
- **Testes**: JUnit 5, RestAssured, H2 (in-memory para testes), Newman (Postman CLI)

## Comandos Essenciais

### Desenvolvimento Local

```bash
# Compilar o projeto (sem testes)
mvn clean package -DskipTests

# Executar a aplicação localmente
mvn spring-boot:run

# Executar somente testes unitários (*Test.java)
mvn test -P unit-tests

# Executar somente testes de integração (*IT.java)
mvn verify -P integration-tests

# Executar todos os testes
mvn verify
```

### Docker

```bash
# Subir a aplicação com Docker Compose (PostgreSQL + App)
docker compose up -d

# Ver logs da aplicação
docker logs verdicomply-api -f

# Ver logs do PostgreSQL
docker logs verdicomply-postgres -f

# Parar containers
docker compose down

# Parar e remover volumes (limpar dados)
docker compose down -v
```

### Testes Automatizados com Newman

```bash
# Executar todos os testes Postman via Newman
newman run postman/verdicomply-api-collection-complete.json \
  -e postman/VerdiComply-Dev.postman_environment.json

# Resultados esperados:
# - 70 assertions passando (100% de sucesso)
# - 41 requisições executadas (100% de sucesso)
# - 0 falhas
# - Tempo médio: ~24ms
```

## Arquitetura e Estrutura

### Arquitetura em Camadas

O projeto segue uma arquitetura em camadas tradicional do Spring:

1. **Controller** (`controller/`): Endpoints REST, validação de entrada, mapeamento HTTP
2. **Service** (`service/` e `service/impl/`): Lógica de negócio
3. **Repository** (`repository/`): Acesso a dados via Spring Data JPA
4. **Model** (`model/`): Entidades JPA
5. **DTO** (`dto/`): Data Transfer Objects para comunicação API

### Domínios Principais

A aplicação possui 4 domínios principais:

- **Auditorias** (`/api/auditorias/**`): Gerenciamento de auditorias ambientais por departamento
- **Conformidades** (`/api/conformidades/**`): Conformidade com normas ambientais por auditoria
- **Pendências** (`/api/pendencias/**`): Ações pendentes vinculadas a conformidades
- **Logs** (`/api/logs/**`): Registro histórico de alterações em conformidades

### Segurança e Autenticação

O sistema implementa segurança JWT com três níveis de acesso:

- **ROLE_AUDITOR**: Visualizar recursos (read-only)
- **ROLE_GESTOR**: Visualizar e gerenciar recursos (CRUD exceto delete)
- **ROLE_ADMIN**: Acesso completo incluindo exclusão

**Componentes de Segurança**:
- `SecurityConfig.java`: Configuração de segurança, CORS e filtros
- `JwtTokenProvider.java`: Geração e validação de tokens JWT
- `JwtAuthenticationFilter.java`: Intercepta requisições e valida tokens
- `DatabaseUserDetailsService.java`: Carrega usuários do banco de dados

**Endpoints Públicos**: `/api/public/**` e `/api/auth/**` não requerem autenticação.

**Obter Token JWT**:
```bash
POST /api/public/auth/login
Body: {"username": "admin", "password": "admin123"}
```

### Migrações Flyway

As migrações estão em `src/main/resources/db/migration/postgresql/`:

- `V1__criar_tabelas_base.sql`: Tabelas principais em lowercase (departamento, auditoria, norma_ambiental, conformidade, pendencia, log_conformidade)
- `V2__inserir_dados_iniciais.sql`: Dados de seed (departamentos, normas ambientais)
- `V3__criar_indices.sql`: Índices para otimização de queries
- `V4__criar_tabela_usuarios.sql`: Tabela de usuários com senhas BCrypt

**Configuração Flyway**: O projeto usa `baseline-on-migrate=true` e convenção lowercase sem aspas para compatibilidade com PostgreSQL.

**IMPORTANTE**: Todas as tabelas e colunas usam **lowercase sem aspas** (auditoria, id_auditoria, data_auditoria, etc.)

### Profiles do Spring

- **default** (local): PostgreSQL local em `localhost:5432`
- **prod**: PostgreSQL Docker em `postgres:5432` (usado no Docker Compose)
- **test**: H2 in-memory (PostgreSQL mode) para testes unitários
- **integracao**: H2 in-memory (PostgreSQL mode) para testes de integração

### Convenções de Testes

- **Testes Unitários**: Sufixo `*Test.java` (ex: `AuditoriaServiceTest.java`)
  - Usam mocks e H2 in-memory
  - Executados pelo Surefire plugin na fase `test`

- **Testes de Integração**: Sufixo `*IT.java` (ex: `AuthControllerIT.java`)
  - Usam `@SpringBootTest` com H2 in-memory
  - Executados pelo Failsafe plugin na fase `integration-test`
  - Testam fluxos completos com RestAssured

## Estrutura de Pacotes

```
com.github.pablowinck.verdicomplyapi/
├── config/              # Configurações Spring (CORS, Database)
├── controller/          # Controllers REST
│   └── exception/       # Exception handlers (ManipuladorGlobalDeExcecoes)
├── dto/                 # Data Transfer Objects
├── model/               # Entidades JPA (lowercase naming)
├── repository/          # Interfaces Spring Data JPA
├── security/            # Configuração JWT e Spring Security
├── service/             # Interfaces de serviço
│   ├── impl/           # Implementações de serviço
│   └── exception/      # Exceções de serviço (RecursoNaoEncontradoException)
```

## Usuários de Teste

Três usuários são criados automaticamente pelas migrações:

- **auditor** / `auditor123` - ROLE_AUDITOR
- **gestor** / `gestor123` - ROLE_GESTOR, ROLE_AUDITOR
- **admin** / `admin123` - ROLE_ADMIN, ROLE_GESTOR, ROLE_AUDITOR

## Banco de Dados

### Conexão PostgreSQL (Docker Compose)

**Configurações padrão**:
- **Host**: `postgres` (dentro da rede Docker) ou `localhost:5433` (acesso externo)
- **Database**: `verdicomply`
- **User**: `verdicomply`
- **Password**: `verdicomply`
- **Porta**: `5432` (interna) / `5433` (externa)

**Nota**: Em produção, altere as credenciais usando variáveis de ambiente.

### Tabelas Principais (lowercase)

- **departamento**: Departamentos organizacionais
- **auditoria**: Auditorias ambientais por departamento
- **norma_ambiental**: Normas e regulamentações ambientais
- **conformidade**: Status de conformidade por auditoria e norma
- **pendencia**: Ações pendentes para resolver não-conformidades
- **log_conformidade**: Histórico de alterações em conformidades
- **usuarios**: Usuários do sistema com senha BCrypt

**IMPORTANTE**: Todas as tabelas e colunas usam **snake_case lowercase** sem aspas.

## Dicas de Desenvolvimento

### Adicionar Nova Entidade

1. Criar a entidade JPA em `model/`
2. Criar o repository em `repository/`
3. Criar o DTO em `dto/`
4. Criar a interface do service em `service/`
5. Implementar o service em `service/impl/`
6. Criar o controller em `controller/`
7. Adicionar migração Flyway se necessário
8. Escrever testes unitários e de integração

### Trabalhar com Migrações

- Flyway exige nomes sequenciais: `V{número}__{descrição}.sql`
- **Nunca** modifique migrações já aplicadas - crie novas
- Use o utilitário `./oracle-util.sh clean` para limpar o esquema em desenvolvimento
- Flyway baseline é automático devido a `baseline-on-migrate=true`

### Testes

- Sempre escreva testes unitários para services
- Teste endpoints com testes de integração usando RestAssured
- Utilize `@WebMvcTest` para testes de controllers isolados
- Utilize `@DataJpaTest` para testes de repositories
- Testes de integração devem usar o profile `integracao`

### Debugging

```bash
# Ver status da aplicação
curl http://localhost:8080/health

# Ver logs do Docker
docker logs verdicomply-api -f

# Verificar conectividade com Oracle
./oracle-util.sh list

# Testar autenticação
curl -X POST http://localhost:8080/api/public/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## Collection Postman

O projeto inclui uma coleção Postman completa em `postman/`:

- `verdicomply-api-collection-complete.json`: Collection com todos os endpoints
- `verdicomply-api-environment.json`: Variáveis de ambiente
- Scripts para preparar ambiente de teste e executar testes automatizados

## Documentação Adicional

- ADRs (Architecture Decision Records) em `docs/adr/`
- Exemplos de chamadas curl em `exemplos-curl.md`
- README principal com informações de setup
