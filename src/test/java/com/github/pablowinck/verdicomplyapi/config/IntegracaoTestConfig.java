package com.github.pablowinck.verdicomplyapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração específica para testes de integração
 */
@TestConfiguration
@Profile("integracao")
public class IntegracaoTestConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(IntegracaoTestConfig.class);
    
    /**
     * Fornece um TestRestTemplate para os testes de integração
     * @return TestRestTemplate configurado
     */
    @Bean
    public TestRestTemplate testRestTemplate() {
        return new TestRestTemplate();
    }
    
    /**
     * Configuração do codificador de senha para testes
     * @return BCryptPasswordEncoder para uso nos testes
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.debug("Criando BCryptPasswordEncoder para o perfil de integração");
        return new BCryptPasswordEncoder();
    }
}
