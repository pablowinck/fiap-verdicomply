package com.github.pablowinck.verdicomplyapi.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DepartamentoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Deve criar um departamento válido")
    void deveCriarDepartamentoValido() {
        // Arrange
        Departamento departamento = Departamento.builder()
                .nomeDepartamento("Manufatura")
                .build();

        // Act
        Set<ConstraintViolation<Departamento>> violations = validator.validate(departamento);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(departamento.getNomeDepartamento()).isEqualTo("Manufatura");
    }

    @Test
    @DisplayName("Deve falhar ao criar um departamento sem nome")
    void deveFalharAoCriarDepartamentoSemNome() {
        // Arrange
        Departamento departamento = Departamento.builder()
                .build();

        // Act
        Set<ConstraintViolation<Departamento>> violations = validator.validate(departamento);

        // Assert
        assertThat(violations).hasSize(1);
        String message = violations.iterator().next().getMessage();
        assertThat(message).matches(".*((must not be null)|(não deve ser nulo)).*");
    }

    @Test
    @DisplayName("Deve conseguir atualizar dados do departamento")
    void deveConseguirAtualizarDadosDoDepartamento() {
        // Arrange
        Departamento departamento = Departamento.builder()
                .nomeDepartamento("Manufatura")
                .build();

        // Act
        departamento.setNomeDepartamento("Logística");

        // Assert
        assertThat(departamento.getNomeDepartamento()).isEqualTo("Logística");
    }
}
