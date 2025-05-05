package com.github.pablowinck.verdicomplyapi.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Configuração de banco de dados específica para testes de integração.
 * Esta classe tem precedência sobre a configuração padrão quando o perfil "integracao" está ativo.
 */
@Configuration
@Profile("integracao")
public class TestDatabaseConfig {

    /**
     * Cria um datasource H2 em memória para uso nos testes de integração
     * com a anotação @Primary para ter precedência sobre outros beans
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("testdb")
                .build();
    }
}
