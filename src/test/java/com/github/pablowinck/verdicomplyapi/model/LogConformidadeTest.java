package com.github.pablowinck.verdicomplyapi.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LogConformidadeTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Deve criar um log de conformidade válido")
    void deveCriarLogConformidadeValido() {
        // Arrange
        Conformidade conformidade = Conformidade.builder()
                .id(1L)
                .build();

        LogConformidade logConformidade = LogConformidade.builder()
                .conformidade(conformidade)
                .acao("INSERÇÃO")
                .dataRegistro(LocalDate.now())
                .detalhes("Conformidade: S, Obs: Todos os filtros estão dentro do padrão")
                .build();

        // Act
        Set<ConstraintViolation<LogConformidade>> violations = validator.validate(logConformidade);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(logConformidade.getConformidade().getId()).isEqualTo(1L);
        assertThat(logConformidade.getAcao()).isEqualTo("INSERÇÃO");
        assertThat(logConformidade.getDetalhes()).isEqualTo("Conformidade: S, Obs: Todos os filtros estão dentro do padrão");
    }

    @Test
    @DisplayName("Deve criar um log de conformidade sem conformidade")
    void deveCriarLogConformidadeSemConformidade() {
        // Arrange
        LogConformidade logConformidade = LogConformidade.builder()
                .acao("INSERÇÃO")
                .dataRegistro(LocalDate.now())
                .detalhes("Teste de log sem conformidade associada")
                .build();

        // Act
        Set<ConstraintViolation<LogConformidade>> violations = validator.validate(logConformidade);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(logConformidade.getAcao()).isEqualTo("INSERÇÃO");
        assertThat(logConformidade.getDetalhes()).isEqualTo("Teste de log sem conformidade associada");
    }
}
