# ADR 001 - Arquitetura da Plataforma VerdiComply

**Título:** Arquitetura da Plataforma de Gestão de Conformidades Ambientais  
**Status:** Aceito  
**Data:** 03/05/2025  
**Decisão Tomada por:** Arquitetura de Sistemas

## Contexto

A empresa precisa garantir conformidade com normas ambientais por meio de auditorias periódicas. Durante essas auditorias, quando uma **não conformidade** é registrada, ações corretivas precisam ser automaticamente geradas, rastreadas e auditadas. A rastreabilidade é fundamental para atender órgãos reguladores como **IBAMA**, **CETESB**, **ANA** e **CONAMA**.

## Decisão

Será construída uma **aplicação web baseada no padrão MVC (Model-View-Controller)** com uso do ecossistema Spring, promovendo separação de responsabilidades, testabilidade e manutenibilidade. A aplicação terá os seguintes pilares:

### Tecnologias Utilizadas

- **Spring Web** para desenvolvimento de APIs REST
- **Spring Data JPA** para acesso ao banco de dados relacional
- **H2 Database** para testes automatizados (ambiente `test`)
- **Lombok** (uso obrigatório) para reduzir boilerplate de getters/setters/construtores

- **Java 17**
- **Spring Boot 3.4.5**
- **Spring Web / Spring MVC**
- **Spring Security com autenticação baseada em roles**
- **Banco de Dados Oracle 19c**
- **Flyway para versionamento do schema**
- **Docker + Docker Compose** para desenvolvimento e testes

## Camadas e Responsabilidades (Padrão MVC)

### 1. **Model**

- Representação do domínio:
  - `Auditoria`
  - `Conformidade`
  - `Pendencia`
  - `LogConformidade`
- Uso de JPA (Hibernate) com boas práticas:
  - Evitar lógica de negócio nos modelos (usar Service para isso)
  - Utilizar `@Entity`, `@Table`, `@Id`, `@GeneratedValue`
  - Auditar entidades com campos `createdAt`, `updatedAt` (via `@PrePersist` / `@PreUpdate`)
  - Respeitar coesão da entidade (uma entidade = um agregado consistente)

### 2. **Controller**

- Interface HTTP com o usuário/sistema
- Anotações:
  - `@RestController` e `@RequestMapping`
  - `@GetMapping`, `@PostMapping`, etc.
- Boas práticas:
  - Delegar lógica ao `Service`, não implementar lógica de negócio aqui
  - Retornar `ResponseEntity<?>`
  - Validar dados de entrada com `@Valid` + DTOs (e.g. `ConformidadeDTO`)

### 3. **Service**

- Contém a **lógica de negócio** do domínio
- Boas práticas:
  - Usar `@Service`
  - Métodos claros e pequenos (`registrarConformidade()`, `verificarPendenciasVencidas()`)
  - Lançamento de exceções específicas (`PendenciaVencidaException`, `AuditoriaNaoEncontradaException`)

### 4. **Repository**

- Camada de persistência
- Interfaces com `JpaRepository` (e.g. `ConformidadeRepository`, `AuditoriaRepository`)
- Consultas customizadas com `@Query` ou QueryDSL

---

## Regras Automatizadas no Banco Oracle

- **Trigger 1:** Após inserir conformidade com `estaConforme = 'N'`, insere pendência com 15 dias
- **Trigger 2:** Após inserir ou atualizar conformidade, gera log de auditoria em `LogConformidade`
- **Procedure 1:** `atualizar_status_auditoria` - atualiza status com base em conformidades
- **Procedure 2:** `verificar_pendencias_vencidas` - lista pendências vencidas no console (alerta)

---

## Segurança

- Autenticação via Spring Security + JWT
- Controle de acesso baseado em roles (`ROLE_AUDITOR`, `ROLE_GESTOR`, `ROLE_ADMIN`)
- Endpoints públicos e privados separados por rotas (`/api/public/**`, `/api/private/**`)

---

## Contêinerização

- Imagem Docker com `Dockerfile` multi-stage (build + runtime)
- `docker-compose.yaml` com serviços:
  - `backend`: aplicação Spring Boot
  - `oracle-db`: banco Oracle 19c (imagem de desenvolvimento)
- Perfis:
  - `application-dev.yml` para dev com Oracle local
  - `application-prod.yml` para uso futuro com Oracle Cloud

---

## Padrões e Boas Práticas Spring

- **Uso obrigatório de Lombok** para reduzir a verbosidade dos modelos (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)

- Utilização de:
  - **Bean Validation**: `@NotNull`, `@Size`, `@Pattern`
  - **OpenAPI** para documentação automática (Swagger)
  - **ControllerAdvice** para tratamento global de exceções
  - **Spring Events** para desacoplar lógica (e.g. log automático via listener)

---

## Consequências

- **Escalabilidade e modularidade** com clara separação entre camadas
- **Rastreabilidade e transparência** para processos regulatórios
- **Baixo acoplamento** e alta testabilidade
- **Evolução facilitada** para microserviços no futuro (via migração do core para módulos REST autônomos)
