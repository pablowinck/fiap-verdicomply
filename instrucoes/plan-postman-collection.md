## Plano para Criação da Collection do Postman

### Etapas Iniciais
- [x] Levantar todos os endpoints disponíveis na aplicação
- [x] Identificar parâmetros e dados necessários para cada endpoint
- [x] Definir estrutura de pastas para organização dos requests
- [x] Definir variáveis de ambiente a serem utilizadas

### Preparação do Ambiente
- [x] Verificar se o Postman CLI está instalado
- [x] Instalar Newman (CLI do Postman)
- [x] Configurar variáveis de ambiente iniciais
- [x] Criar estrutura base da collection

### Implementação dos Endpoints por Área
- [x] Implementar endpoints de autenticação
  - [x] Login (cenários positivos e negativos)
  - [x] Registro (cenários positivos e negativos)
  
- [x] Implementar endpoints de auditoria
  - [x] Listar auditorias
  - [x] Buscar auditoria por ID
  - [x] Criar auditoria
  - [x] Atualizar auditoria
  - [x] Deletar auditoria
  
- [x] Implementar endpoints de conformidade
  - [x] Listar conformidades
  - [x] Buscar conformidade por ID
  - [x] Criar conformidade
  - [x] Atualizar conformidade
  - [x] Deletar conformidade
  
- [x] Implementar endpoints de pendência
  - [x] Listar pendências
  - [x] Buscar pendência por ID
  - [x] Criar pendência
  - [x] Atualizar pendência
  - [x] Deletar pendência
  
- [x] Implementar endpoints de log de conformidade
  - [x] Listar logs
  - [x] Buscar log por ID
  - [x] Criar log
  
- [x] Implementar endpoint de health check

### Combinação da Collection
- [x] Desenvolver script para combinar todos os endpoints
- [x] Criar arquivo de collection completo

### Testes via cURL
- [x] Criar script de teste com cURL para testar a API sequencialmente
- [x] Testar endpoint de health check
- [x] Testar endpoints de autenticação
- [x] Testar endpoints de auditoria
- [ ] Testar cenários negativos para validação

### Problemas Identificados (durante execução)
- [x] Problema na validação de data futura no endpoint de auditoria
- [x] Formato incorreto do campo estaConforme na conformidade (S/N vs boolean)
- [x] Normas ambientais não encontradas no banco de dados
- [x] Implementar controlador REST para normas ambientais
- [x] Implementar inicializador de normas ambientais
- [x] Depurar erro 500 no endpoint de normas ambientais

### Testes via CLI do Postman (Newman)
- [x] Testar endpoint de health check
- [x] Testar endpoints de autenticação
- [x] Testar endpoints de auditoria
- [x] Testar endpoints de normas ambientais
- [x] Testar endpoints de conformidade
- [x] Testar endpoints de pendência
- [x] Testar endpoints de log de conformidade

### Correções e Melhorias
- [x] Corrigir script para extrair IDs corretamente das respostas JSON
- [x] Criar script simplificado para testar apenas funcionalidades básicas
- [x] Criar script para inicializar normas ambientais no sistema
- [x] Criar controlador REST para acesso a normas ambientais
- [x] Criar script final de testes com resumo de resultados
- [x] Corrigir JWT token handling no Newman

### Implementações Realizadas
- [x] Script shell em cURL para testar a API de forma integrada
- [x] Controlador de Normas Ambientais para completar os endpoints da API
- [x] Scripts de debug para análise de respostas JSON
- [x] Inicializador de dados para ambiente de produção e testes

### Resultados dos Testes
- [x] Health Check: 100% funcionando
- [x] Autenticação (Login/Registro): 100% funcionando
- [x] Auditoria: 100% funcionando
- [x] Normas Ambientais: 100% funcionando
- [x] Conformidade: 100% funcionando
- [x] Pendência: 100% funcionando
- [x] Log Conformidade: 100% funcionando

### Execução de Testes Automatizados
- [x] Executar testes unitários com o perfil unit-tests
- [x] Executar testes integrados com o perfil integration-tests
- [x] Verificar cobertura dos testes unitários
- [x] Garantir que todas as classes modificadas possuem testes
- [x] Atualizar testes existentes para considerar os novos campos de NormaAmbiental

### Documentação e Finalização
- [x] Revisar collection completa
- [x] Adicionar descrições claras para cada request
- [x] Garantir que todos os cenários positivos e negativos estão cobertos
- [x] Criar scripts completos para testes via linha de comando
- [x] Documentar todos os endpoints disponíveis no sistema
- [x] Garantir que 100% dos endpoints estão cobertos na collection Postman
- [x] Exportar collection finalizada para compartilhamento
