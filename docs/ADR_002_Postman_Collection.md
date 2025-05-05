# ADR 002: Coleção Postman para Testes e Documentação da API

## Contexto

A API VerdiComply possui uma variedade de endpoints para autenticação, gerenciamento de auditorias ambientais, normas, conformidades e pendências. Com o crescimento da complexidade da API, surgiu a necessidade de uma abordagem sistemática para testes, documentação e exploração desses endpoints.

## Decisão

Decidimos criar uma coleção completa de Postman que:

1. Cobre todos os endpoints da API
2. Implementa fluxos de autenticação automatizados
3. Inclui exemplos de requisições e respostas para cada endpoint
4. Fornece scripts de testes para validação automática
5. Utiliza variáveis de ambiente para facilitar a troca entre ambientes (desenvolvimento, homologação, produção)

## Justificativa

### Benefícios

- **Exploração Simplificada**: Permite que desenvolvedores e stakeholders explorem a API sem precisar memorizar endpoints ou formatos de requisição
- **Documentação Viva**: A coleção serve como documentação interativa que é mantida junto com o código
- **Testes Automáticos**: Facilita a criação e execução de testes de integração de forma rápida e consistente
- **Onboarding**: Reduz o tempo necessário para novos membros da equipe entenderem e utilizarem a API
- **Validação de Mudanças**: Permite testar rapidamente o impacto de mudanças na API

### Alternativas Consideradas

1. **Swagger/OpenAPI**: Embora ofereça boa documentação, não possui a mesma facilidade para testes interativos que o Postman
2. **Scripts de Teste Customizados**: Exigiriam mais manutenção e não ofereceriam uma interface amigável
3. **Insomnia**: Similar ao Postman, mas com menor adoção no mercado e na comunidade

## Estrutura da Coleção

A coleção foi organizada em pastas lógicas, seguindo a estrutura da API:

1. **Autenticação**
   - Login
   - Registro
   - Refresh Token

2. **Auditorias**
   - Listar todas
   - Obter por ID
   - Criar nova
   - Atualizar existente
   - Excluir

3. **Normas Ambientais**
   - Listar todas
   - Obter por ID
   - Criar nova
   - Atualizar existente
   - Excluir

4. **Conformidades**
   - Listar todas
   - Obter por ID
   - Criar nova
   - Atualizar existente
   - Excluir

5. **Pendências**
   - Listar todas
   - Obter por ID
   - Criar nova
   - Atualizar existente
   - Excluir

6. **Logs**
   - Listar logs de conformidade
   - Filtrar por período

## Manutenção

Para manter a coleção atualizada:

1. Novas funcionalidades da API devem incluir a atualização correspondente na coleção Postman
2. Alterações em endpoints existentes devem ser refletidas na coleção
3. A coleção deve ser versionada junto com a API
4. Testes automatizados utilizando a coleção devem ser executados como parte do pipeline de CI/CD

## Consequências

### Positivas

- Facilidade de testes manuais e automatizados
- Documentação atualizada e interativa
- Melhoria na qualidade da API através de testes consistentes
- Redução no tempo de descoberta e resolução de problemas

### Negativas

- Necessidade de manter a coleção sincronizada com a implementação da API
- Dependência de uma ferramenta externa (Postman)

## Status

Aprovado

## Autores

- Time de Desenvolvimento VerdiComply

## Data

Data de criação: Agosto de 2023
Última atualização: Agosto de 2023
