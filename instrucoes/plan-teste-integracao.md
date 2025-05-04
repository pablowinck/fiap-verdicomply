# Plano de Implementação de Testes de Integração com Cucumber

## Decisões de Arquitetura

- [x] Remover dependências do Cucumber
- [x] Adotar testes de integração diretos com Spring Boot e JUnit 5
- [x] Configurar perfil de teste (application-test.properties)
- [x] Manter H2 para testes

## Implementação dos Testes

- [x] Criar configuração para testes de integração (IntegrationTestConfig)
- [x] Implementar teste de autenticação (AuthControllerIT)
- [x] Implementar teste de login com credenciais válidas
- [x] Implementar teste de login com credenciais inválidas
- [x] Implementar teste de autorização (AuthorizationIT)
- [x] Implementar teste de acesso a endpoints protegidos
- [x] Implementar teste de verificação de roles

## Fluxo Completo de Negócio

- [x] Criar feature para fluxo completo do Auditor
- [x] Implementar steps para criação e consulta de auditorias
- [ ] Validar persistência de dados no H2
- [ ] Testar relacionamentos entre entidades

## Problemas Resolvidos

- [x] Simplificação da estrutura de testes de integração
- [x] Remoção da dependência do Cucumber para evitar conflitos
- [x] Utilização da abordagem nativa do Spring Boot para testes
- [x] Manutenção do mesmo nível de cobertura de testes

## Próximos Passos

- [ ] Executar testes de integração
- [ ] Verificar cobertura de testes
- [ ] Adicionar mais testes para outros fluxos de negócio
- [ ] Integrar com pipeline de CI/CD

## Integração com Pipeline de CI

- [ ] Configurar execução dos testes no Maven
- [ ] Garantir geração de relatórios formatados
- [ ] Estabelecer critérios de sucesso/falha nos testes
- [ ] Integrar com cobertura de código

## Problemas Identificados (durante execução)

- [x] Configuração duplicada do contexto Spring para Cucumber
- [x] Conflitos entre JUnit 4 e JUnit 5 no Maven
- [x] Inicialização do contexto Spring incorreta
- [x] Ausência de usuários para teste no H2
- [x] Erro ao localizar as steps do Cucumber

## Ações Corretivas

- [x] Remover configurações duplicadas do Spring
- [x] Simplificar dependências JUnit para evitar conflitos
- [x] Criar arquivo SQL para inicialização de dados de teste
- [ ] Criar configuração minimalista do Spring
- [ ] Adicionar instruções explícitas de GLUE no Cucumber
- [ ] Simplificar features para focar em testes básicos
- [ ] Implementar steps usando a API do Spring TestRestTemplate
