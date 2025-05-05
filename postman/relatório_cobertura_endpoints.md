# Relatório de Cobertura de Endpoints da API VerdiComply

## Visão Geral
Este relatório confirma a cobertura completa de todos os endpoints da API VerdiComply na coleção Postman.

## Endpoints por Categoria

### Autenticação e Registro
- [x] `POST /api/public/auth/login` - Login de usuários
- [x] `POST /api/public/registro` - Registro de novos usuários

### Health Check
- [x] `GET /api/public/health` - Verificação da saúde da API

### Normas Ambientais
- [x] `GET /api/normas` - Listar todas as normas
- [x] `GET /api/normas/{id}` - Buscar norma por ID
- [x] `GET /api/normas/orgao/{orgaoFiscalizador}` - Buscar normas por órgão fiscalizador
- [x] `GET /api/normas/codigo/{codigoNorma}` - Buscar norma por código
- [x] `POST /api/normas` - Criar nova norma
- [x] `PUT /api/normas/{id}` - Atualizar norma existente
- [x] `DELETE /api/normas/{id}` - Excluir norma

### Auditorias
- [x] `GET /api/auditorias` - Listar todas as auditorias
- [x] `GET /api/auditorias/{id}` - Buscar auditoria por ID
- [x] `GET /api/auditorias/status/{status}` - Buscar auditorias por status
- [x] `GET /api/auditorias/departamento/{departamentoId}` - Buscar auditorias por departamento
- [x] `POST /api/auditorias` - Criar nova auditoria
- [x] `PUT /api/auditorias/{id}` - Atualizar auditoria existente
- [x] `DELETE /api/auditorias/{id}` - Excluir auditoria

### Conformidades
- [x] `GET /api/conformidades` - Listar todas as conformidades
- [x] `GET /api/conformidades/{id}` - Buscar conformidade por ID
- [x] `GET /api/conformidades/auditoria/{auditoriaId}` - Buscar conformidades por auditoria
- [x] `GET /api/conformidades/status/{estaConforme}` - Buscar conformidades por status
- [x] `GET /api/conformidades/norma/{normaAmbientalId}` - Buscar conformidades por norma
- [x] `POST /api/conformidades` - Criar nova conformidade
- [x] `PUT /api/conformidades/{id}` - Atualizar conformidade existente
- [x] `DELETE /api/conformidades/{id}` - Excluir conformidade

### Pendências
- [x] `GET /api/pendencias` - Listar todas as pendências
- [x] `GET /api/pendencias/{id}` - Buscar pendência por ID
- [x] `GET /api/pendencias/conformidade/{conformidadeId}` - Buscar pendências por conformidade
- [x] `GET /api/pendencias/status/{resolvida}` - Buscar pendências por status
- [x] `GET /api/pendencias/vencidas` - Listar pendências vencidas
- [x] `POST /api/pendencias` - Criar nova pendência
- [x] `PUT /api/pendencias/{id}` - Atualizar pendência existente
- [x] `DELETE /api/pendencias/{id}` - Excluir pendência

### Logs de Conformidade
- [x] `GET /api/logs` - Listar todos os logs
- [x] `GET /api/logs/{id}` - Buscar log por ID
- [x] `GET /api/logs/conformidade/{conformidadeId}` - Buscar logs por conformidade
- [x] `GET /api/logs/acao/{acao}` - Buscar logs por ação
- [x] `POST /api/logs` - Criar novo log
- [x] `DELETE /api/logs/{id}` - Excluir log

## Resumo da Cobertura

| Categoria | Total de Endpoints | Cobertos | Cobertura |
|-----------|-------------------|----------|-----------|
| Autenticação | 2 | 2 | 100% |
| Health Check | 1 | 1 | 100% |
| Normas Ambientais | 7 | 7 | 100% |
| Auditorias | 7 | 7 | 100% |
| Conformidades | 8 | 8 | 100% |
| Pendências | 8 | 8 | 100% |
| Logs | 6 | 6 | 100% |
| **Total** | **39** | **39** | **100%** |

## Conclusão

A coleção Postman atual cobre 100% dos endpoints expostos pela API VerdiComply, incluindo:
- Cenários positivos para todos os endpoints
- Cenários negativos para validação de erros
- Testes automatizados para verificar o comportamento da API
- Variáveis de ambiente para facilitar a execução em diferentes ambientes

Todos os endpoints foram testados e funcionam conforme esperado.

Data da verificação: 05/05/2025
