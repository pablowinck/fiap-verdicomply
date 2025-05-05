package com.github.pablowinck.verdicomplyapi.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Classe utilitária para criar um usuário administrador no primeiro startup
 */
@Configuration
public class CreateAdminUser {

    @Bean
    @Profile("prod") // Executar apenas no perfil prod
    public CommandLineRunner createAdminRunner(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {
        
        return args -> {
            System.out.println("Verificando usuário admin...");
            
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                // Verifica se o usuário admin já existe
                boolean adminExists = false;
                try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM USUARIOS WHERE USERNAME = ?")) {
                    ps.setString(1, "admin");
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            adminExists = rs.getInt(1) > 0;
                        }
                    }
                }
                
                // Se não existe, cria o usuário admin
                if (!adminExists) {
                    System.out.println("Criando usuário admin...");
                    
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    String encodedPassword = encoder.encode("password");
                    
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO USUARIOS (USERNAME, PASSWORD, ROLE) VALUES (?, ?, ?)")) {
                        ps.setString(1, "admin");
                        ps.setString(2, encodedPassword);
                        ps.setString(3, "ADMIN");
                        int result = ps.executeUpdate();
                        
                        if (result > 0) {
                            System.out.println("Usuário admin criado com sucesso!");
                        } else {
                            System.err.println("Erro ao criar usuário admin");
                        }
                    }
                } else {
                    System.out.println("Usuário admin já existe, nenhuma ação necessária.");
                }
            } catch (Exception e) {
                System.err.println("Erro ao verificar/criar usuário admin: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
