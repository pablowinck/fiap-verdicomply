# Utilitário Oracle FIAP

Ferramenta auxiliar para gerenciar o esquema Oracle da FIAP, com funcionalidades para depuração e ajuste dos scripts de migração Flyway.

## Funcionalidades

- **Listar tabelas** - Exibe todas as tabelas existentes no esquema
- **Verificar controle do Flyway** - Analisa o estado das migrações Flyway
- **Limpar esquema** - Remove todas as tabelas, incluindo controles do Flyway
- **Executar SQL personalizado** - Interface para execução de comandos SQL
- **Verificar migrações** - Valida o estado das tabelas esperadas pela aplicação

## Pré-requisitos

- Node.js 14.x ou superior
- Acesso ao banco de dados Oracle da FIAP

## Instalação

1. Acesse o diretório da ferramenta:

```bash
cd scripts/node-oracle-util
```

2. Instale as dependências:

```bash
npm install
```

3. Configure as variáveis de ambiente no arquivo `.env`

## Uso

### Interface Interativa

Para iniciar o menu interativo:

```bash
npm start
```

### Limpeza do Esquema

Para limpar o esquema (remover todas as tabelas):

```bash
# Listar tabelas sem excluir
npm run clean -- --list

# Excluir tabelas específicas (requer confirmação)
npm run clean -- --tables NORMA_AMBIENTAL USUARIOS

# Excluir todas as tabelas (requer confirmação)
npm run clean

# Excluir sem confirmação (cuidado!)
npm run clean -- --force
```

## Fluxo de Trabalho Recomendado

1. Use `npm start` e verifique o estado atual do banco
2. Execute a limpeza do esquema se necessário
3. Ajuste os scripts de migração Flyway conforme erros encontrados
4. Execute a aplicação Spring Boot para aplicar as migrações
5. Verifique os logs e repita o ciclo até que todas as migrações sejam aplicadas com sucesso

---

**Atenção:** Esta ferramenta pode realizar operações destrutivas no banco de dados. Use com cautela!
