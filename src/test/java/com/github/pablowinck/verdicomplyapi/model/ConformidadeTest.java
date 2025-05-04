package com.github.pablowinck.verdicomplyapi.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConformidadeTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Deve criar uma conformidade válida")
    void deveCriarConformidadeValida() {
        // Arrange
        Auditoria auditoria = Auditoria.builder()
                .id(1L)
                .build();

        NormaAmbiental norma = NormaAmbiental.builder()
                .id(1L)
                .build();

        Conformidade conformidade = Conformidade.builder()
                .auditoria(auditoria)
                .norma(norma)
                .estaConforme("S")
                .observacao("Todos os filtros estão dentro do padrão")
                .build();

        // Act
        Set<ConstraintViolation<Conformidade>> violations = validator.validate(conformidade);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(conformidade.getAuditoria()).isEqualTo(auditoria);
        assertThat(conformidade.getNorma()).isEqualTo(norma);
        assertThat(conformidade.getEstaConforme()).isEqualTo("S");
        assertThat(conformidade.getObservacao()).isEqualTo("Todos os filtros estão dentro do padrão");
    }

    @Test
    @DisplayName("Deve falhar ao criar uma conformidade sem auditoria")
    void deveFalharAoCriarConformidadeSemAuditoria() {
        // Arrange
        NormaAmbiental norma = NormaAmbiental.builder()
                .id(1L)
                .build();

        Conformidade conformidade = Conformidade.builder()
                .norma(norma)
                .estaConforme("S")
                .observacao("Todos os filtros estão dentro do padrão")
                .build();

        // Act
        Set<ConstraintViolation<Conformidade>> violations = validator.validate(conformidade);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("não deve ser nulo");
    }

    @Test
    @DisplayName("Deve falhar ao criar uma conformidade sem norma")
    void deveFalharAoCriarConformidadeSemNorma() {
        // Arrange
        Auditoria auditoria = Auditoria.builder()
                .id(1L)
                .build();

        Conformidade conformidade = Conformidade.builder()
                .auditoria(auditoria)
                .estaConforme("S")
                .observacao("Todos os filtros estão dentro do padrão")
                .build();

        // Act
        Set<ConstraintViolation<Conformidade>> violations = validator.validate(conformidade);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("não deve ser nulo");
    }

    @Test
    @DisplayName("Deve falhar com valor inválido para estaConforme")
    void deveFalharComValorInvalidoParaEstaConforme() {
        // Arrange
        Auditoria auditoria = Auditoria.builder()
                .id(1L)
                .build();

        NormaAmbiental norma = NormaAmbiental.builder()
                .id(1L)
                .build();

        Conformidade conformidade = Conformidade.builder()
                .auditoria(auditoria)
                .norma(norma)
                .estaConforme("X") // Valor inválido, deve ser S ou N
                .observacao("Todos os filtros estão dentro do padrão")
                .build();

        // Act
        Set<ConstraintViolation<Conformidade>> violations = validator.validate(conformidade);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("O valor deve ser 'S' para conforme ou 'N' para não conforme");
    }
}
