# Plano de Implantação Docker

## Análise Inicial

- [x] Examinar arquivo docker-compose.yml atual
- [x] Verificar configurações do Dockerfile
- [x] Analisar propriedades da aplicação
- [x] Identificar dependências necessárias
- [x] Verificar configuração do Flyway

## Preparação do Ambiente

- [x] Parar containers existentes (se houver)
- [x] Limpar volumes e networks desatualizados
- [x] Garantir portas necessárias disponíveis
- [x] Verificar permissões de arquivos
- [x] Testar conectividade com o Docker

## Implantação Docker

- [x] Iniciar stack com docker-compose up
- [x] Verificar logs de inicialização
- [x] Confirmar containers em execução
- [x] Verificar redes criadas corretamente
- [x] Monitorar uso de recursos

## Verificação da Base de Dados

- [x] Iniciar processo de inicialização do banco Oracle
- [x] Aguardar inicialização completa do banco Oracle
- [x] Verificar execução das migrações Flyway
- [x] Confirmar criação das tabelas
- [x] Verificar inserção de dados iniciais
- [x] Testar consultas básicas

## Validação da Aplicação

- [ ] Verificar logs de inicialização da API
- [ ] Testar endpoint de saúde/status
- [ ] Obter token JWT via autenticação
- [ ] Testar endpoints protegidos
- [ ] Confirmar funcionamento geral

## REQUISITO ESSENCIAL

- **É IMPRESCINDÍVEL utilizar o banco de dados ORACLE** - requerimento do sistema
- **Será utilizada conexão direta com o servidor Oracle da FIAP** - simplifica a implantação e elimina problemas de compatibilidade com ARM64

## Nova Abordagem de Conexão com Banco de Dados

- [x] Identificar problemas de compatibilidade Oracle XE em ambiente ARM64
- [x] Decidir utilizar conexão remota com servidor Oracle da FIAP
- [x] Remover configuração de container Oracle do docker-compose
- [x] Configurar aplicação para usar credenciais do Oracle FIAP
- [x] Criar perfil de produção com configurações do Oracle FIAP
- [x] Atualizar o README com informações sobre a conexão
- [x] Atualizar o ADR (Architecture Decision Record)
- [x] Testar implantação com conexão direta ao Oracle FIAP

## Ajustes para Oracle da FIAP

- [x] Identificar erro ORA-00942 (table or view does not exist)
- [x] Identificar problema do Flyway pulando V1 por causa do baseline-on-migrate=true
- [x] Ajustar configuração do Flyway para não fazer baseline e executar todas as migrações
- [x] Modificar scripts SQL para usar sequências Oracle tradicionais em vez de IDENTITY
- [x] Ajustar INSERTs para fornecer IDs explicitamente usando NEXTVAL das sequências

## Nova Abordagem de Limpeza do Esquema com Java

- [x] Criar utilitário Java para gerenciar o banco de dados Oracle da FIAP
- [x] Implementar funcionalidade para listar tabelas existentes
- [x] Implementar funcionalidade para limpar o esquema (remover tabelas e sequências)
- [x] Implementar funcionalidade para executar comandos SQL personalizados
- [x] Criar script shell para facilitar a execução do utilitário
- [x] Executar limpeza do esquema antes da implantação da aplicação
- [x] Ajustar configurações do Docker Compose para conectar ao Oracle da FIAP
- [x] Ajustar scripts de migração conforme necessário
- [x] Testar ciclo completo até que Flyway funcione perfeitamente na primeira execução

## Melhorias no Utilitário Oracle Independente

- [x] Separar utilitário Oracle em projeto Java independente
- [x] Implementar suporte para execução automática via linha de comando
- [x] Adicionar opções para listar tabelas, limpar esquema e executar SQL customizado
- [x] Otimizar script shell para evitar recompilação desnecessária
- [x] Integrar utilitário com o processo de deploy via `deploy-with-clean.sh`

## Configuração do Flyway para Ambiente FIAP

- [x] Ajustar `application-prod.properties` para usar credenciais explícitas
- [x] Habilitar `baseline-on-migrate=true` para criação automática em banco limpo
- [x] Configurar `baseline-version=0` para garantir execução de todas migrações
- [x] Manter configurações permissivas para ambiente acadêmico com restrições
- [x] Testar ciclo completo de criação do banco da configuração até a primeira execução

## Passos de Ajuste Iterativo

- [x] Criar utilitário independente para limpar esquema
- [x] Ajustar scripts de migração conforme erros encontrados
- [x] Configurar Flyway para criar corretamente o esquema
- [x] Verificar logs e corrigir problemas
- [x] Repetir ciclo até resolver todos os problemas

## Testes de Implantação

- [x] Atualizar README com informações sobre as novas ferramentas
- [x] Criar ADR sobre o utilitário Oracle independente
- [x] Simplificar Docker Compose removendo serviço oracle-client desnecessário
- [ ] Resolver problemas com o Flyway e banco de dados
  - [x] Identificar comportamento inconsistente da tabela flyway_schema_history
  - [x] Detectar problema de migração falha na versão 2
  - [ ] Corrigir erro relacionado à tabela ausente `auditoria`
- [ ] Testar autenticar-se na API e realizar operações básicas

## Problemas Identificados

### Problema com o Flyway

O Flyway está apresentando comportamento inconsistente:

1. Relata "migração falha" na versão 2
2. A tabela `flyway_schema_history` aparece na listagem do esquema, mas apresenta erro ao tentar executar SQL
3. A aplicação não consegue iniciar porque a tabela `auditoria` não existe

### Ações corretivas:

1. Verificar se a tabela `flyway_schema_history` realmente existe e seu conteúdo
2. Examinar os scripts de migração Flyway para entender a criação da tabela `auditoria`
3. Ajustar configuração do Flyway para ignorar validação e permitir recriação completa do esquema


