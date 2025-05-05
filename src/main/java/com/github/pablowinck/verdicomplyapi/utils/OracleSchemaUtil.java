package com.github.pablowinck.verdicomplyapi.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Utilitário para manutenção do esquema Oracle.
 * Execute como: java -Dspring.profiles.active=util -jar verdicomplyapi.jar
 */
@Component
@Profile("util")
public class OracleSchemaUtil implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public OracleSchemaUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(OracleSchemaUtil.class)
                .web(WebApplicationType.NONE)
                .profiles("util")
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Verificar se deve executar limpeza automática
        String autoClean = System.getProperty("schema.clean");
        if (autoClean != null && autoClean.equalsIgnoreCase("true")) {
            System.out.println("\n=== LIMPEZA AUTOMÁTICA DO ESQUEMA ORACLE ===");
            System.out.println("Iniciando limpeza automática do esquema...");
            cleanSchema();
            System.out.println("Limpeza automática concluída. Encerrando...");
            return;
        }
        
        // Modo interativo normal
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== UTILITÁRIO DE GERENCIAMENTO DO BANCO ORACLE ===");
                System.out.println("1. Listar tabelas");
                System.out.println("2. Limpar esquema (CUIDADO: remove todas as tabelas e sequências)");
                System.out.println("3. Executar SQL personalizado");
                System.out.println("4. Sair");
                
                System.out.print("\nEscolha uma opção: ");
                String option = scanner.nextLine();
                
                switch (option) {
                    case "1":
                        listTables();
                        break;
                    case "2":
                        System.out.print("Tem certeza que deseja limpar o esquema? (S/N): ");
                        String confirm = scanner.nextLine();
                        if (confirm.equalsIgnoreCase("S")) {
                            cleanSchema();
                        }
                        break;
                    case "3":
                        System.out.print("Digite o comando SQL: ");
                        String sql = scanner.nextLine();
                        executeQuery(sql);
                        break;
                    case "4":
                        System.out.println("Saindo...");
                        return;
                    default:
                        System.out.println("Opção inválida!");
                }
            }
        } // Scanner é fechado automaticamente aqui
    }
    
    private void listTables() {
        try {
            List<Map<String, Object>> tables = jdbcTemplate.queryForList("SELECT table_name FROM user_tables ORDER BY table_name");
            
            System.out.println("\nTabelas encontradas:");
            
            if (tables.isEmpty()) {
                System.out.println("Nenhuma tabela encontrada.");
            } else {
                for (Map<String, Object> row : tables) {
                    System.out.println("- " + row.get("TABLE_NAME"));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar tabelas: " + e.getMessage());
        }
    }
    
    private void cleanSchema() {
        try {
            // Desabilitar todas as constraints para permitir a remoção
            System.out.println("Desabilitando restrições...");
            try {
                jdbcTemplate.execute("BEGIN\n" +
                        "    FOR c IN (SELECT table_name, constraint_name FROM user_constraints WHERE constraint_type = 'R') LOOP\n" +
                        "        EXECUTE IMMEDIATE 'ALTER TABLE ' || c.table_name || ' DISABLE CONSTRAINT ' || c.constraint_name;\n" +
                        "    END LOOP;\n" +
                        "END;");
            } catch (Exception e) {
                System.err.println("Aviso ao desabilitar restrições: " + e.getMessage());
            }
            
            // Listar e dropar todas as tabelas
            System.out.println("Removendo tabelas...");
            List<Map<String, Object>> tables = jdbcTemplate.queryForList("SELECT table_name FROM user_tables");
            for (Map<String, Object> row : tables) {
                String tableName = (String) row.get("TABLE_NAME");
                try {
                    jdbcTemplate.execute("DROP TABLE " + tableName + " CASCADE CONSTRAINTS");
                    System.out.println("- Tabela " + tableName + " removida.");
                } catch (Exception e) {
                    System.err.println("- Erro ao remover tabela " + tableName + ": " + e.getMessage());
                }
            }
            
            // Listar e dropar todas as sequências
            System.out.println("Removendo sequências...");
            List<Map<String, Object>> sequences = jdbcTemplate.queryForList("SELECT sequence_name FROM user_sequences");
            for (Map<String, Object> row : sequences) {
                String seqName = (String) row.get("SEQUENCE_NAME");
                try {
                    jdbcTemplate.execute("DROP SEQUENCE " + seqName);
                    System.out.println("- Sequência " + seqName + " removida.");
                } catch (Exception e) {
                    System.err.println("- Erro ao remover sequência " + seqName + ": " + e.getMessage());
                }
            }
            
            System.out.println("Esquema limpo com sucesso!");
            
        } catch (Exception e) {
            System.err.println("Erro ao limpar esquema: " + e.getMessage());
        }
    }
    
    private void executeQuery(String sql) {
        try {
            if (sql.trim().toLowerCase().startsWith("select")) {
                List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
                
                if (results.isEmpty()) {
                    System.out.println("A consulta não retornou resultados.");
                    return;
                }
                
                // Imprimir nomes das colunas
                Map<String, Object> firstRow = results.get(0);
                for (String columnName : firstRow.keySet()) {
                    System.out.print(columnName + "\t");
                }
                System.out.println();
                
                // Imprimir linhas
                for (Map<String, Object> row : results) {
                    for (Object value : row.values()) {
                        System.out.print((value != null ? value.toString() : "NULL") + "\t");
                    }
                    System.out.println();
                }
                
                System.out.println("\nConsulta executada com sucesso: " + results.size() + " linha(s) retornada(s).");
            } else {
                int rowsAffected = jdbcTemplate.update(sql);
                System.out.println("Comando executado com sucesso: " + rowsAffected + " linha(s) afetada(s).");
            }
        } catch (Exception e) {
            System.err.println("Erro ao executar SQL: " + e.getMessage());
        }
    }
}

/**
 * Configuração específica para o perfil de utilitário
 */
@Configuration
@Profile("util")
class OracleSchemaUtilConfig {
    // Configurações específicas para o utilitário, se necessário
}
