package com.github.pablowinck.verdicomplyapi.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Classe utilitária para gerar hashes de senhas para testes
 * Esta classe não depende do contexto Spring
 */
public class GeradorSenhasUtil {
    
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Gera hash BCrypt para uma senha
     * 
     * @param senha Senha em texto plano
     * @return Hash BCrypt
     */
    public static String gerarHash(String senha) {
        return passwordEncoder.encode(senha);
    }
    
    /**
     * Gera comandos SQL para inserção de usuários com senhas
     * 
     * @param senhas Array de senhas para gerar hashes
     * @return Comandos SQL formatados
     */
    public static String gerarScriptUsuarios(String[] senhas) {
        StringBuilder sql = new StringBuilder();
        sql.append("-- Dados para testes de integração (H2)\n\n");
        sql.append("-- Inserção de usuários para testes\n");
        sql.append("INSERT INTO usuarios (username, password, role)\n");
        sql.append("VALUES\n");
        
        for (int i = 0; i < senhas.length; i++) {
            String senha = senhas[i];
            String role = senha.toUpperCase();
            String hash = gerarHash(senha);
            
            sql.append("    ('" + senha + "', '" + hash + "', '" + role + "')")
               .append(i < senhas.length - 1 ? "," : ";")
               .append(" -- Senha: " + senha + "\n");
        }
        
        return sql.toString();
    }
    
    /**
     * Método utilitário para exibir hashes no console
     * 
     * @param senhas Array de senhas para gerar hashes
     */
    public static void exibirHashesSenhas(String[] senhas) {
        System.out.println("\n\n");
        System.out.println("INICIANDO GERAÇÃO DE HASHES");
        System.out.println("============================");
        
        for (String senha : senhas) {
            String hash = gerarHash(senha);
            System.out.println("SENHA_" + senha.toUpperCase() + "=" + hash);
        }
    }
}
