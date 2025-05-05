package com.github.pablowinck.verdicomplyapi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração específica para testes de integração
 * Esta classe garante que todos os beans necessários para os testes de integração
 * sejam registrados corretamente no contexto da aplicação
 */
@TestConfiguration
@Profile("integracao")
@Import({TestConfig.class, TestDatabaseConfig.class})
public class IntegrationTestConfig {
    
    /**
     * Bean primário de encoder para testes de integração
     */
    @Bean
    @Primary
    @Profile("integracao")
    public PasswordEncoder integrationPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
