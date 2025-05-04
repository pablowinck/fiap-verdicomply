# Exemplos de Chamadas da API com curl

## Autenticação

### Login
```bash
curl -X POST http://localhost:8080/api/public/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin", "password":"admin123"}'
```

Resposta esperada:
```json
{
  "tipo": "Bearer",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "admin",
  "roles": ["ROLE_ADMIN", "ROLE_GESTOR", "ROLE_AUDITOR"]
}
```

## Auditorias

### Listar todas as auditorias
```bash
curl -X GET http://localhost:8080/api/auditorias \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### Obter auditoria por ID
```bash
curl -X GET http://localhost:8080/api/auditorias/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### Criar nova auditoria
```bash
curl -X POST http://localhost:8080/api/auditorias \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "departamentoId": 1,
    "dataAuditoria": "2025-05-15",
    "auditorResponsavel": "Maria Silva",
    "statusAuditoria": "PENDENTE"
  }'
```

### Atualizar auditoria
```bash
curl -X PUT http://localhost:8080/api/auditorias/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "departamentoId": 1,
    "dataAuditoria": "2025-05-15",
    "auditorResponsavel": "Maria Silva",
    "statusAuditoria": "CONCLUÍDA"
  }'
```

### Excluir auditoria
```bash
curl -X DELETE http://localhost:8080/api/auditorias/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

## Conformidades

### Listar todas as conformidades
```bash
curl -X GET http://localhost:8080/api/conformidades \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### Obter conformidade por ID
```bash
curl -X GET http://localhost:8080/api/conformidades/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### Criar nova conformidade
```bash
curl -X POST http://localhost:8080/api/conformidades \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "auditoriaId": 1,
    "normaAmbientalId": 1,
    "estaConforme": "S",
    "observacoes": "Conforme com a norma ambiental"
  }'
```

## Pendências

### Listar todas as pendências
```bash
curl -X GET http://localhost:8080/api/pendencias \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### Listar pendências vencidas
```bash
curl -X GET "http://localhost:8080/api/pendencias/vencidas?data=2025-05-15" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### Criar pendência
```bash
curl -X POST http://localhost:8080/api/pendencias \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "conformidadeId": 1,
    "descricao": "Instalar filtros de ar",
    "prazoResolucao": "2025-06-15",
    "resolvida": "N"
  }'
```

## Logs de Conformidade

### Listar todos os logs
```bash
curl -X GET http://localhost:8080/api/logs \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### Buscar logs por conformidade
```bash
curl -X GET http://localhost:8080/api/logs/conformidade/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```
