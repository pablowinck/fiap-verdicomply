package com.github.pablowinck.oracle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Utilitário para gerenciar o esquema Oracle usado para testes com Flyway.
 * Permite listar tabelas, limpar o esquema e executar SQL customizado.
 * 
 * Uso via linha de comando:
 * - java -jar oracle-util.jar                         # Modo interativo
 * - java -jar oracle-util.jar clean                   # Limpar esquema
 * - java -jar oracle-util.jar list                    # Listar tabelas
 * - java -jar oracle-util.jar execute "SELECT * FROM tabela"  # Executar SQL
 */
public class OracleSchemaUtil {

    // Configuração da conexão Oracle - FIAP
    private static final String JDBC_URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl";
    private static final String USERNAME = "RM557024";
    private static final String PASSWORD = "240200";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public static void main(String[] args) {
        // Verificar argumentos de linha de comando
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "clean":
                    System.out.println("Executando limpeza automática do esquema...");
                    limparEsquema();
                    return;
                case "init":
                    System.out.println("Inicializando banco de dados com estrutura completa...");
                    inicializarBanco();
                    return;
                case "list":
                    System.out.println("Listando tabelas do esquema...");
                    listarTabelas();
                    return;
                case "execute":
                    if (args.length > 1) {
                        System.out.println("Executando SQL: " + args[1]);
                        executarSqlDireto(args[1]);
                    } else {
                        System.out.println("Erro: Comando SQL não fornecido.");
                        exibirAjuda();
                    }
                    return;
                case "help":
                    exibirAjuda();
                    return;
                default:
                    System.out.println("Comando desconhecido: " + args[0]);
                    exibirAjuda();
                    return;
            }
        }

        // Sem argumentos, inicia o modo interativo
        iniciarModoInterativo();
    }
    
    private static void exibirAjuda() {
        System.out.println("\nUtilitário de Gerenciamento do Esquema Oracle");
        System.out.println("Uso: java -jar oracle-util.jar [comando] [argumentos]");
        System.out.println("\nComandos disponíveis:");
        System.out.println("  (sem comando) - Inicia o modo interativo");
        System.out.println("  clean        - Limpa o esquema (remove todas as tabelas e sequências)");
        System.out.println("  init         - Inicializa o banco com todas as tabelas e dados básicos");
        System.out.println("  list         - Lista todas as tabelas e sequências");
        System.out.println("  execute \"SQL\" - Executa o comando SQL especificado");
        System.out.println("  help         - Exibe esta ajuda\n");
    }
    
    private static void iniciarModoInterativo() {
        Scanner scanner = new Scanner(System.in);
        boolean sair = false;

        while (!sair) {
            exibirMenu();
            int opcao = lerOpcao(scanner);

            switch (opcao) {
                case 1 -> listarTabelas();
                case 2 -> limparEsquema();
                case 3 -> inicializarBanco();
                case 4 -> executarSqlCustomizado(scanner);
                case 0 -> sair = true;
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }

        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("\n===== GERENCIADOR DE ESQUEMA ORACLE =====");
        System.out.println("1. Listar tabelas no esquema");
        System.out.println("2. Limpar esquema (remover todas as tabelas e sequências)");
        System.out.println("3. Inicializar banco com tabelas e dados iniciais");
        System.out.println("4. Executar SQL customizado");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static int lerOpcao(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void listarTabelas() {
        System.out.println("\n--- TABELAS NO ESQUEMA ---");
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT table_name FROM user_tables ORDER BY table_name")) {
            
            boolean temTabelas = false;
            while (rs.next()) {
                System.out.println(rs.getString("table_name"));
                temTabelas = true;
            }
            
            if (!temTabelas) {
                System.out.println("Nenhuma tabela encontrada no esquema.");
            }

            // Listar também as sequências
            System.out.println("\n--- SEQUÊNCIAS NO ESQUEMA ---");
            try (ResultSet rsSeq = stmt.executeQuery(
                    "SELECT sequence_name FROM user_sequences ORDER BY sequence_name")) {
                
                boolean temSequencias = false;
                while (rsSeq.next()) {
                    System.out.println(rsSeq.getString("sequence_name"));
                    temSequencias = true;
                }
                
                if (!temSequencias) {
                    System.out.println("Nenhuma sequência encontrada no esquema.");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar tabelas: " + e.getMessage());
        }
    }

    /**
     * Limpa o esquema do banco Oracle
     * Dropa todas as tabelas e sequências do usuário atual
     * Utiliza script SQL dedicado para garantir remoção completa, incluindo metadados Flyway
     */
    private static void limparEsquema() {
        System.out.println("\n--- LIMPANDO ESQUEMA ---");
        try (Connection conn = getConnection()) {
            // Primeiro, tenta executar o script SQL dedicado de limpeza
            try {
                executeResourceScript(conn, "clean_all.sql");
                System.out.println("\nEsquema limpo com sucesso usando script dedicado!");
                return;
            } catch (Exception e) {
                System.out.println("Aviso: Não foi possível executar script de limpeza dedicado. Usando método alternativo.");
                System.out.println("Detalhes: " + e.getMessage());
            }
            
            // Método alternativo - limpeza manual das tabelas principais
            try (Statement stmt = conn.createStatement()) {
                // Tentar remover tabela de histórico do Flyway diretamente
                try {
                    stmt.execute("DROP TABLE flyway_schema_history CASCADE CONSTRAINTS");
                    System.out.println("Tabela Flyway removida: flyway_schema_history");
                } catch (SQLException e) {
                    System.out.println("Erro ao remover tabela flyway_schema_history: " + e.getMessage());
                }
                
                // Obter as tabelas no esquema
                List<String> tabelas = new ArrayList<>();
                try (ResultSet rs = stmt.executeQuery("SELECT TABLE_NAME FROM USER_TABLES")) {
                    while (rs.next()) {
                        tabelas.add(rs.getString("TABLE_NAME"));
                    }
                }

                // Dropa todas as tabelas
                for (String tabela : tabelas) {
                    try {
                        String sql = "DROP TABLE " + tabela + " CASCADE CONSTRAINTS";
                        stmt.executeUpdate(sql);
                        System.out.println("Tabela removida: " + tabela);
                    } catch (SQLException e) {
                        System.out.println("Erro ao remover tabela " + tabela + ": " + e.getMessage());
                    }
                }

                // Obter as sequências no esquema
                List<String> sequencias = new ArrayList<>();
                try (ResultSet rs = stmt.executeQuery("SELECT SEQUENCE_NAME FROM USER_SEQUENCES")) {
                    while (rs.next()) {
                        sequencias.add(rs.getString("SEQUENCE_NAME"));
                    }
                }
                
                // Dropa todas as sequências
                for (String sequencia : sequencias) {
                    try {
                        stmt.executeUpdate("DROP SEQUENCE " + sequencia);
                        System.out.println("Sequência removida: " + sequencia);
                    } catch (SQLException e) {
                        System.out.println("Erro ao remover sequência " + sequencia + ": " + e.getMessage());
                    }
                }
            }
            
            System.out.println("\nEsquema limpo com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao limpar esquema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Executa um script SQL a partir do diretório de recursos
     * @param conn Conexão com o banco de dados
     * @param resourceName Nome do arquivo no diretório de recursos
     */
    private static void executeResourceScript(Connection conn, String resourceName) throws SQLException, IOException {
        // Ler o conteúdo do script SQL do diretório de recursos
        InputStream is = OracleSchemaUtil.class.getClassLoader().getResourceAsStream(resourceName);
        if (is == null) {
            throw new IOException("Script SQL não encontrado: " + resourceName);
        }
        
        String sqlScript = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        is.close();
        
        // Divide o script em comandos individuais pela quebra de linha e ponto e vírgula
        String[] sqlCommands = sqlScript.split(";");
        int successCount = 0;
        int failCount = 0;
        
        // Auto commit desligado para permitir rollback se necessário
        boolean originalAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        
        try (Statement stmt = conn.createStatement()) {
            for (String sqlCommand : sqlCommands) {
                // Remove espaços em branco extras e pula comandos vazios ou comentários
                sqlCommand = sqlCommand.trim();
                if (sqlCommand.isEmpty() || sqlCommand.startsWith("--")) {
                    continue;
                }
                
                try {
                    // Executa cada comando individualmente
                    stmt.executeUpdate(sqlCommand);
                    successCount++;
                } catch (SQLException e) {
                    failCount++;
                    // Mostra apenas os primeiros 100 caracteres do comando para não sobrecarregar o console
                    String commandPreview = sqlCommand.substring(0, Math.min(100, sqlCommand.length()));
                    if (sqlCommand.length() > 100) commandPreview += "...";
                    
                    System.out.println("Aviso: Falha ao executar: " + commandPreview);
                    System.out.println("Erro: " + e.getMessage());
                    
                    // Continua a execução mesmo com erro em um comando
                }
            }
            
            // Commit das alterações se houver sucesso em algum comando
            if (successCount > 0) {
                conn.commit();
            } else if (failCount > 0) {
                // Rollback se todos os comandos falharem
                conn.rollback();
            }
            
            System.out.println(resourceName + " processado. " + successCount + " comandos executados com sucesso. " + failCount + " falhas.");
        } catch (Exception e) {
            // Rollback em caso de erro não esperado
            conn.rollback();
            throw e;
        } finally {
            // Restaura configuração original de autocommit
            conn.setAutoCommit(originalAutoCommit);
        }
    
    }
    
    /**
     * Inicializa o banco de dados com todas as tabelas e dados iniciais
     * Executa o script full_init.sql que combina todas as migrações em uma única operação
     */
    private static void inicializarBanco() {
        System.out.println("\n--- INICIALIZANDO BANCO DE DADOS ---");
        try (Connection conn = getConnection()) {
            // Primeiro limpar o esquema para garantir que não haja resquícios
            System.out.println("Realizando limpeza do esquema antes da inicialização...");
            limparEsquema();
            
            // Executar script completo de inicialização
            System.out.println("\nExecutando script de inicialização completa...");
            try {
                executeResourceScript(conn, "full_init.sql");
                System.out.println("\nBanco de dados inicializado com sucesso!");
                System.out.println("Estrutura completa criada com todas as tabelas, sequências, triggers e dados iniciais.");
            } catch (IOException e) {
                System.out.println("Erro ao carregar script de inicialização: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Executa SQL fornecido via parâmetro linha de comando
     */
    private static void executarSqlDireto(String sql) {
        System.out.println("\n--- EXECUTANDO SQL CUSTOMIZADO ---");
        System.out.println("SQL fornecido: " + sql);
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            boolean isQuery = sql.toLowerCase().trim().startsWith("select");
            
            if (isQuery) {
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    // Imprimir cabeçalho
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(metaData.getColumnName(i) + "\t");
                    }
                    System.out.println();
                    
                    // Imprimir linha separadora
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print("--------\t");
                    }
                    System.out.println();
                    
                    // Imprimir dados
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.print(rs.getString(i) + "\t");
                        }
                        System.out.println();
                    }
                }
            } else {
                int affected = stmt.executeUpdate(sql);
                System.out.println("Comando executado com sucesso! " + affected + " linha(s) afetada(s).");
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao executar SQL: " + e.getMessage());
        }
    }
    
    private static void executarSqlCustomizado(Scanner scanner) {
        System.out.println("\n--- EXECUTAR SQL CUSTOMIZADO ---");
        System.out.println("Digite seu comando SQL (ou 'voltar' para retornar ao menu):");
        
        String sql = scanner.nextLine().trim();
        if ("voltar".equalsIgnoreCase(sql)) {
            return;
        }
        
        executarSqlDireto(sql);
    }
}
