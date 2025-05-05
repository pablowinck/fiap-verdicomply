package com.github.pablowinck.verdicomplyapi.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Configuração específica para o perfil "util".
 * Desabilita o auto-configuration do Hibernate/JPA e configura um DataSource
 * simples.
 */
@Configuration
@Profile("util")
@EnableAutoConfiguration(exclude = { HibernateJpaAutoConfiguration.class })
public class UtilConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        dataSource.setUrl("jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl");
        dataSource.setUsername("RM557024");
        dataSource.setPassword("240200");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
