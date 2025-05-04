package com.github.pablowinck.verdicomplyapi.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Classe de configuração específica para testes
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.github.pablowinck.verdicomplyapi")
@EntityScan(basePackages = "com.github.pablowinck.verdicomplyapi.model")
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
