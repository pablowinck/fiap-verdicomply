# ADR 001: Conexão Direta com Oracle da FIAP

## Status

Aceito

## Contexto

A aplicação VerdiComply necessita de um banco de dados Oracle para sua operação. Inicialmente, a abordagem proposta era usar um contêiner Docker com Oracle Express Edition (XE) para facilitar a implantação.

Durante a implantação, encontramos problemas de compatibilidade do Oracle XE em ambientes ARM64 (Apple Silicon), resultando em erros como:
- `ORA-00443: background process "PMON" did not start`
- Falhas na inicialização do banco mesmo com configurações de emulação x86_64

## Decisão

Optamos por conectar diretamente ao servidor Oracle da FIAP, eliminando a necessidade de executar um contêiner Oracle localmente. Essa abordagem proporciona:

1. Compatibilidade completa com todos os ambientes, incluindo máquinas ARM64
2. Desempenho aprimorado, eliminando a sobrecarga de emulação
3. Simplificação da arquitetura de implantação
4. Redução dos requisitos de hardware para execução do projeto

A conexão utiliza as seguintes configurações:
```
URL: jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl
Usuário: RM557024
Senha: 240200
```

## Consequências

### Positivas

- Eliminação dos problemas de compatibilidade em arquiteturas ARM64
- Implantação mais rápida e simples
- Redução do consumo de recursos locais
- Maior estabilidade do ambiente de desenvolvimento

### Negativas

- Dependência do servidor Oracle da FIAP e sua disponibilidade
- Necessidade de conexão de rede para desenvolvimento e testes
- Compartilhamento do banco com outros usuários do servidor FIAP

## Notas

Para ambientes de produção, seria recomendável usar um provedor de banco de dados gerenciado ou implementar mecanismos para gerenciar credenciais de forma segura, em vez de armazená-las diretamente no código ou arquivos de configuração.
