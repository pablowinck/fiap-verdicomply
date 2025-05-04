package com.github.pablowinck.verdicomplyapi.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.github.pablowinck.verdicomplyapi.config.IntegracaoTestApplication;

/**
 * Classe utilitária para gerar hashes de senhas para testes
 */
@SpringBootTest(classes = IntegracaoTestApplication.class)
@ActiveProfiles("integracao")
public class GerarSenhasTest {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    void gerarHashesSenhas() throws Exception {
        // Senhas dos usuários para testes
        String[] senhas = {"admin", "gestor", "auditor"};
        
        System.out.println("\n\n");
        System.out.println("INICIANDO GERACAO DE HASHES");
        System.out.println("============================");
        
        StringBuilder sql = new StringBuilder();
        sql.append("-- Dados para testes de integracao (H2)\n\n");
        sql.append("-- Insercao de usuarios para testes\n");
        sql.append("INSERT INTO usuarios (username, password, role)\n");
        sql.append("VALUES\n");
        
        for (int i = 0; i < senhas.length; i++) {
            String senha = senhas[i];
            String role = senha.toUpperCase();
            String hash = passwordEncoder.encode(senha);
            
            System.out.println("SENHA_" + senha.toUpperCase() + "=" + hash);
            
            sql.append("    ('" + senha + "', '" + hash + "', '" + role + "')")
               .append(i < senhas.length - 1 ? "," : ";")
               .append(" -- Senha: " + senha + "\n");
        }
        
        sql.append("\n-- Insercao de departamentos para testes (com nomes unicos para evitar duplicacao)\n");
        sql.append("INSERT INTO departamento (nome_departamento)\n");
        sql.append("VALUES\n");
        sql.append("    ('Manufatura_Integracao'),\n");
        sql.append("    ('Logística_Integracao'),\n");
        sql.append("    ('Operações_Integracao');\n");
        
        String sqlContent = sql.toString();
        System.out.println("\n\nARQUIVO SQL GERADO:\n\n" + sqlContent);
        
        // Salvar o arquivo SQL gerado
        try {
            java.nio.file.Path filePath = java.nio.file.Paths.get("src/test/resources/data-integracao-new.sql");
            java.nio.file.Files.writeString(filePath, sqlContent, java.nio.charset.StandardCharsets.UTF_8);
            System.out.println("\nArquivo SQL salvo em: " + filePath.toAbsolutePath());
        } catch (Exception e) {
            System.out.println("Erro ao salvar arquivo SQL: " + e.getMessage());
        }
        
        System.out.println("\nFIM DA GERACAO DE HASHES");
        System.out.println("============================");
    }
}
