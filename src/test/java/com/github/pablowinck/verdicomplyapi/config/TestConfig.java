package com.github.pablowinck.verdicomplyapi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração para testes de integração e unitários
 * Essa configuração garante que os beans necessários para os testes estejam disponíveis
 */
@TestConfiguration
public class TestConfig {

    /**
     * Bean de encoder para testes (usado tanto em testes unitários quanto integração)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
