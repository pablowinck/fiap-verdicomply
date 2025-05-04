# Plano de Implementação dos Endpoints RESTful

## Análise Inicial

- [x] Analisar modelo de domínio e tabelas
- [x] Revisar regras de negócio do ADR
- [x] Definir endpoints necessários a implementar
- [x] Estudar migrações existentes
- [x] Verificar dependências no pom.xml

## Preparação do Ambiente

- [x] Atualizar pom.xml com dependências necessárias
  - [x] Spring Security
  - [x] JWT
  - [x] Bean Validation
  - [x] Springdoc OpenAPI
- [x] Configurar Spring Security
  - [x] Configurar autenticação básica
  - [x] Configurar controle de acesso baseado em papéis
  - [x] Criar usuários em memória para testes
- [x] Configurar documentação OpenAPI
- [ ] Configurar Docker e Docker Compose
- [ ] Configurar conexão com Oracle
- [x] Criar perfis de configuração (dev, test)

## Implementação do Domínio - Modelo e Repositórios

- [x] Criar entidades JPA usando Lombok
  - [x] NormaAmbiental
  - [x] Departamento
  - [x] Auditoria
  - [x] Conformidade
  - [x] Pendencia
  - [x] LogConformidade
- [x] Implementar testes unitários de entidades
  - [x] NormaAmbientalTest
  - [x] DepartamentoTest
  - [x] AuditoriaTest
  - [x] ConformidadeTest
  - [x] PendenciaTest
  - [x] LogConformidadeTest
- [x] Criar repositórios com JPA
  - [x] NormaAmbientalRepository
  - [x] DepartamentoRepository
  - [x] AuditoriaRepository
  - [x] ConformidadeRepository
  - [x] PendenciaRepository
  - [x] LogConformidadeRepository
- [x] Implementar testes de integração de repositórios
  - [x] NormaAmbientalRepositoryTest
  - [x] DepartamentoRepositoryTest
  - [x] AuditoriaRepositoryTest
  - [x] ConformidadeRepositoryTest
  - [x] PendenciaRepositoryTest
  - [x] LogConformidadeRepositoryTest
- [x] Adicionar consultas customizadas necessárias
  - [x] findByCodigoNorma, findByOrgaoFiscalizador em NormaAmbientalRepository
  - [x] findByNomeDepartamento em DepartamentoRepository
  - [x] findByStatusAuditoria, findByDepartamento em AuditoriaRepository
  - [x] findByAuditoria, findByEstaConforme, findByNorma em ConformidadeRepository
  - [x] findByConformidade, findByResolvida, findByResolvidaAndPrazoResolucaoBefore em PendenciaRepository
  - [x] findByConformidadeOrderByDataRegistroDesc, findByAcao em LogConformidadeRepository

## Endpoint 1: Gestão de Auditorias

- [x] Criar DTO de Auditoria
- [x] Criar testes para AuditoriaController
- [x] Implementar AuditoriaService (interface)
- [x] Implementar AuditoriaServiceImpl
- [x] Testar AuditoriaService
- [x] Implementar AuditoriaController
- [x] Adicionar validações e segurança
- [ ] Testar endpoint completo

## Problemas e Melhorias Futuras

- [ ] Refatorar código para melhorar a reutilização
- [ ] Implementar cache para melhorar performance
- [ ] Implementar mecanismo de auditoria de chamadas
- [ ] Adicionar throttling para evitar abusos
- [ ] Adaptar testes para funcionarem com a autenticação JWT
  - A implementação da segurança JWT está funcionando em ambiente de produção
  - Os testes existentes precisam ser adaptados para lidar com autenticação JWT
  - Foi adicionada uma configuração de teste (`TestSecurityConfig`), mas ainda requer ajustes

## Endpoint 2: Gestão de Conformidades

- [x] Criar DTO de Conformidade
- [x] Criar testes para ConformidadeController
- [x] Implementar ConformidadeService (interface)
- [x] Implementar ConformidadeServiceImpl
- [ ] Testar ConformidadeService
- [x] Implementar ConformidadeController
- [x] Adicionar validações e segurança
- [ ] Testar endpoint completo

## Endpoint 3: Gestão de Pendências

- [x] Criar DTO de Pendência
- [x] Criar testes para PendenciaController
- [x] Implementar PendenciaService (interface)
- [x] Implementar PendenciaServiceImpl
- [ ] Testar PendenciaService
- [x] Implementar PendenciaController
- [x] Adicionar validações e segurança
- [ ] Testar endpoint completo

## Endpoint 4: Relatórios e Logs de Conformidade

- [x] Criar DTO de LogConformidade
- [x] Criar testes para LogConformidadeController
- [x] Implementar LogConformidadeService (interface)
- [x] Implementar LogConformidadeServiceImpl
- [ ] Testar LogConformidadeService
- [x] Implementar LogConformidadeController
- [x] Adicionar validações e segurança
- [ ] Testar endpoint completo

## Configuração de Segurança

- [x] Implementar modelo de usuário e perfis (usuários em memória para testes)
- [x] Criar testes de segurança
  - [x] Implementar testes para AuthController
- [x] Configurar JWT
  - [x] Implementar JwtProperties
  - [x] Implementar JwtTokenProvider
  - [x] Implementar JwtAuthenticationFilter
  - [x] Configurar endpoint de autenticação
- [x] Implementar autenticação básica
- [x] Configurar rotas protegidas com @PreAuthorize
- [x] Testar autenticação e autorização

## Contêinerização da Aplicação

- [x] Criar Dockerfile
- [x] Configurar Docker Compose
- [x] Ajustar configurações para o ambiente dockerizado
  - [x] Configurar variáveis de ambiente para banco de dados
  - [x] Configurar variáveis de ambiente para JWT
- [x] Documentar passos para execução via Docker
- [x] Configurar rede entre serviços
- [x] Testar a execução completa
- [x] Documentar processo de inicialização

## Documentação e Finalização

- [x] Atualizar README com instruções
- [x] Adicionar documentação OpenAPI (Swagger)
- [x] Criar exemplos de chamadas com curl
- [ ] Verificar cobertura de testes
- [ ] Revisar código e corrigir code smells
- [x] Validar Docker Compose para uso em CI/CD
