package com.github.pablowinck.verdicomplyapi.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Aplicação de teste específica para o perfil de integração
 * Configura componentes essenciais para os testes de integração
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.github.pablowinck.verdicomplyapi")
@EntityScan(basePackages = "com.github.pablowinck.verdicomplyapi.model")
@EnableJpaRepositories(basePackages = "com.github.pablowinck.verdicomplyapi.repository")
@Import({IntegracaoTestConfig.class})
public class IntegracaoTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegracaoTestApplication.class, args);
    }
}
