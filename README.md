# VerdiComply API

## Sobre o Projeto

VerdiComply é uma aplicação RESTful para gerenciamento de auditorias ambientais, permitindo o acompanhamento de conformidades com normas ambientais e o gerenciamento de pendências.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.4.5
- Spring Security (Autenticação JWT + Controle de Acesso Baseado em Papéis)
- Spring Data JPA
- Oracle Database (servidor FIAP)
- Flyway para migrações de banco de dados
- Docker e Docker Compose para conteinerização

## Pré-requisitos

- Docker e Docker Compose
- Java 17 (para desenvolvimento local)
- Maven 3.9.9 (para desenvolvimento local)

## Como Executar

### Usando Docker Compose

1. Clone o repositório:

   ```bash
   git clone https://github.com/pablowinck/verdicomplyapi.git
   cd verdicomplyapi
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

A aplicação utiliza conexão direta com o servidor Oracle da FIAP com as seguintes configurações:

- **URL**: `jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl`
- **Usuário**: `RM557024`
- **Senha**: `240200`

> **Nota**: Estas credenciais são exclusivas para o ambiente de demonstração e desenvolvimento. Em ambiente de produção, use váriaveis de ambiente ou serviços de gerenciamento de configuração seguros.

### Utilitário de Gerenciamento Oracle

A aplicação fornece um utilitário Java independente para gerenciar o banco de dados Oracle:

```bash
# Visualizar opções disponíveis
./oracle-util.sh help

# Listar tabelas e sequências existentes
./oracle-util.sh list

# Limpar todo o esquema (remove tabelas e sequências)
./oracle-util.sh clean

# Executar SQL personalizado
./oracle-util.sh execute "SELECT * FROM USUARIOS"
```

### Configuração do Flyway

O projeto utiliza Flyway para gerenciar migrações de banco de dados. As migrações estão configuradas para:

- Realizar baseline automático quando necessário
- Executar scripts em ordem correta
- Funcionar mesmo em ambientes Oracle com restrições de permissões

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
