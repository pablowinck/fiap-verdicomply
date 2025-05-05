# ADR 002: Utilitário Oracle Independente e Configuração Flyway

## Status

Aceito

## Contexto

A aplicação VerdiComply precisa gerenciar o banco Oracle da FIAP para desenvolvimento e testes, incluindo a capacidade de limpar o esquema, examinar o estado atual do banco e aplicar migrações via Flyway. Enfrentamos os seguintes desafios:

1. Dificuldade em realizar limpeza completa do esquema Oracle para testes
2. Problemas na configuração do Flyway para funcionar adequadamente no primeiro uso
3. Necessidade de um mecanismo independente para executar operações de DDL e DML no banco
4. Restrições de acesso no banco Oracle da FIAP que exigem configurações específicas

## Decisão

Tomamos as seguintes decisões para resolver estes problemas:

### 1. Utilitário Oracle Independente

Criamos um utilitário Java standalone para gerenciar o banco Oracle, com as seguintes características:

- Implementado como um projeto Java independente com dependência mínima (apenas driver JDBC Oracle)
- Fornece funcionalidades para listar tabelas e sequências existentes
- Permite limpar completamente o esquema (remover tabelas e sequências)
- Suporta execução de SQL customizado para diagnóstico e operações específicas
- Interface de linha de comando com comandos intuitivos (`clean`, `list`, `execute`, etc.)
- Independente da aplicação principal, evitando conflitos de dependências

### 2. Configuração Flyway Otimizada

Ajustamos as configurações do Flyway para funcionar corretamente com o banco Oracle da FIAP:

- Habilitamos `baseline-on-migrate=true` para criação automática da estrutura Flyway
- Configuramos `baseline-version=0` para garantir a execução de todas as migrações
- Definimos permissões flexíveis para contornar as limitações do ambiente Oracle da FIAP
- Configuramos conexão direta sem depender de variáveis do Spring Boot

## Consequências

### Positivas

- Capacidade de realizar ciclos completos de desenvolvimento e teste com banco limpo
- Independência do utilitário Oracle em relação ao framework Spring
- Diagnóstico e resolução de problemas simplificados
- Interface de linha de comando intuitiva para operações de banco
- Integração perfeita com o fluxo de implantação via Docker Compose
- Configuração Flyway robusta que funciona na primeira execução

### Negativas

- Duplicação de código de conexão de banco entre o utilitário e a aplicação principal
- Credenciais de banco codificadas diretamente no utilitário
- Dependência de scripts shell para integração completa

## Implementação

O utilitário Oracle foi implementado como um projeto Java separado em `scripts/oracle-util/`, com seu próprio arquivo `pom.xml` e classe principal `OracleSchemaUtil.java`.

A configuração Flyway otimizada foi definida em `application-prod.properties`.

Para integração, criamos o script `oracle-util.sh` que compila e executa o utilitário, além de `deploy-with-clean.sh` que automatiza todo o processo de implantação.

## Notas

Esta solução foi projetada especificamente para o ambiente acadêmico da FIAP. Em um ambiente de produção, seria recomendável:

1. Implementar gerenciamento seguro de credenciais usando variáveis de ambiente ou serviços de gerenciamento de segredos
2. Considerar uma estratégia mais robusta para versionamento e controle de acesso ao banco de dados
3. Implementar logs e monitoramento mais abrangentes para operações críticas de banco
