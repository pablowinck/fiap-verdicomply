# EcoTrack Auditor API

## Sobre o Projeto

EcoTrack Auditor é uma aplicação RESTful para gerenciamento de auditorias ambientais, permitindo o acompanhamento de conformidades com normas ambientais e o gerenciamento de pendências.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.4.5
- Spring Security (Autenticação JWT + Controle de Acesso Baseado em Papéis)
- Spring Data JPA
- Oracle 19c
- Flyway para migrações de banco de dados
- Docker e Docker Compose para contêinerização
- Swagger/OpenAPI para documentação da API

## Pré-requisitos

- Docker e Docker Compose
- Java 17 (para desenvolvimento local)
- Maven 3.9.9 (para desenvolvimento local)

## Como Executar

### Usando Docker Compose

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/verdicomplyapi.git
   cd verdicomplyapi
   ```

2. Execute a aplicação utilizando Docker Compose:
   ```bash
   docker-compose up -d
   ```

3. Acesse a documentação da API:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

### Desenvolvimento Local

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/verdicomplyapi.git
   cd verdicomplyapi
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

Todos os endpoints estão protegidos e requerem autenticação, com exceção de `/api/public/**`, `/swagger-ui/**` e `/v3/api-docs/**`.

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
