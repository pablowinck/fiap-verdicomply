# 📸 Guia para Tirar Prints da Pipeline

Este documento contém instruções passo a passo para tirar prints dos resultados da aplicação para documentação.

## ✅ Pré-requisitos

Certifique-se de que a aplicação está rodando:

```bash
docker compose up -d
```

## 🎯 Prints Principais

### 1. Aplicação Rodando (Docker)

**Comando:**
```bash
docker ps
```

**Print deve mostrar:**
- Container `verdicomply-api` (aplicação)
- Container `verdicomply-postgres` (banco de dados)
- Status: `Up` e `healthy`

---

### 2. Logs da Aplicação

**Comando:**
```bash
docker logs verdicomply-api --tail 30
```

**Print deve mostrar:**
- Mensagem: `Started VerdicomplyapiApplication in X.XXX seconds`
- Porta: `Tomcat started on port 8080`
- Sem erros

---

### 3. Health Check da API

**Comando:**
```bash
curl http://localhost:8080/api/public/health
```

**Print deve mostrar:**
```json
{"status":"UP"}
```

---

### 4. Testes Newman - 100% de Sucesso ⭐

**Comando:**
```bash
./run-tests.sh
```

**OU diretamente:**
```bash
newman run postman/verdicomply-api-collection-complete.json \
  -e postman/VerdiComply-Dev.postman_environment.json \
  --timeout-request 10000
```

**Print deve mostrar:**

```
┌─────────────────────────┬───────────────────┬──────────────────┐
│                         │          executed │           failed │
├─────────────────────────┼───────────────────┼──────────────────┤
│              iterations │                 1 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│                requests │                41 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│            test-scripts │                82 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│      prerequest-scripts │                41 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│              assertions │                70 │                0 │
└─────────────────────────┴───────────────────┴──────────────────┘
```

**✅ Destaque:** 70 assertions passando, 0 falhas (100% de sucesso)

---

### 5. Autenticação JWT

**Comando:**
```bash
curl -X POST http://localhost:8080/api/public/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}'
```

**Print deve mostrar:**
```json
{
  "tipo": "Bearer",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "admin",
  "roles": ["ROLE_ADMIN", "ROLE_GESTOR", "ROLE_AUDITOR"]
}
```

---

### 6. Banco de Dados PostgreSQL

**Comando:**
```bash
docker exec -it verdicomply-postgres psql -U verdicomply -d verdicomply -c "\dt"
```

**Print deve mostrar:**
Lista de tabelas:
- auditoria
- conformidade
- departamento
- flyway_schema_history
- log_conformidade
- norma_ambiental
- pendencia
- usuarios

---

### 7. Migrações Flyway

**Comando:**
```bash
docker exec -it verdicomply-postgres psql -U verdicomply -d verdicomply -c "SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;"
```

**Print deve mostrar:**
```
 version |         description          | success
---------+------------------------------+---------
 1       | criar tabelas base           | t
 2       | inserir dados iniciais       | t
 3       | criar indices                | t
 4       | criar tabela usuarios        | t
```

---

### 8. Build da Aplicação

**Comando:**
```bash
mvn clean package -DskipTests
```

**Print deve mostrar:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: X.XXX s
```

---

### 9. Testes Unitários

**Comando:**
```bash
mvn test -P unit-tests
```

**Print deve mostrar:**
```
[INFO] Tests run: XX, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

### 10. Docker Compose Status

**Comando:**
```bash
docker compose ps
```

**Print deve mostrar:**
```
NAME                     STATUS                   PORTS
verdicomply-api          Up X minutes (healthy)   0.0.0.0:8080->8080/tcp
verdicomply-postgres     Up X minutes (healthy)   0.0.0.0:5433->5432/tcp
```

---

## 🚀 Sequência Recomendada para Prints

1. `docker compose up -d` (subir aplicação)
2. `docker ps` (verificar containers)
3. `docker logs verdicomply-api --tail 30` (logs de inicialização)
4. `curl http://localhost:8080/api/public/health` (health check)
5. `./run-tests.sh` (testes Newman - **PRINCIPAL**)
6. Print de autenticação JWT
7. Print das tabelas do banco
8. Print das migrações Flyway

---

## 📊 Métricas Chave para Documentar

- ✅ **100% de sucesso nos testes** (70/70 assertions)
- ✅ **41 requisições testadas**
- ✅ **0 falhas**
- ✅ **PostgreSQL 16**
- ✅ **Spring Boot 3.4.5**
- ✅ **Java 17**
- ✅ **Docker + Docker Compose**
- ✅ **Flyway para migrações**
- ✅ **JWT para autenticação**

---

## 💡 Dicas

- Use `--tail` nos logs do Docker para mostrar apenas as últimas linhas
- Capture a tabela de resultados do Newman por completo (é o print mais importante)
- Mostre claramente que `assertions: 70 | 0` (70 passando, 0 falhando)
- Capture o tempo de resposta médio (~24ms)
