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

class PendenciaTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Deve criar uma pendência válida")
    void deveCriarPendenciaValida() {
        // Arrange
        Conformidade conformidade = Conformidade.builder()
                .id(1L)
                .build();

        Pendencia pendencia = Pendencia.builder()
                .conformidade(conformidade)
                .descricaoPendencia("Separar resíduos químicos e orgânicos corretamente")
                .prazoResolucao(LocalDate.now().plusDays(15))
                .resolvida("N")
                .build();

        // Act
        Set<ConstraintViolation<Pendencia>> violations = validator.validate(pendencia);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(pendencia.getConformidade()).isEqualTo(conformidade);
        assertThat(pendencia.getDescricaoPendencia()).isEqualTo("Separar resíduos químicos e orgânicos corretamente");
        assertThat(pendencia.getResolvida()).isEqualTo("N");
    }

    @Test
    @DisplayName("Deve falhar ao criar uma pendência sem conformidade")
    void deveFalharAoCriarPendenciaSemConformidade() {
        // Arrange
        Pendencia pendencia = Pendencia.builder()
                .descricaoPendencia("Separar resíduos químicos e orgânicos corretamente")
                .prazoResolucao(LocalDate.now().plusDays(15))
                .resolvida("N")
                .build();

        // Act
        Set<ConstraintViolation<Pendencia>> violations = validator.validate(pendencia);

        // Assert
        assertThat(violations).hasSize(1);
        String message = violations.iterator().next().getMessage();
        assertThat(message).matches(".*((must not be null)|(não deve ser nulo)).*");
    }

    @Test
    @DisplayName("Deve falhar com valor inválido para resolvida")
    void deveFalharComValorInvalidoParaResolvida() {
        // Arrange
        Conformidade conformidade = Conformidade.builder()
                .id(1L)
                .build();

        Pendencia pendencia = Pendencia.builder()
                .conformidade(conformidade)
                .descricaoPendencia("Separar resíduos químicos e orgânicos corretamente")
                .prazoResolucao(LocalDate.now().plusDays(15))
                .resolvida("X") // Valor inválido, deve ser S ou N
                .build();

        // Act
        Set<ConstraintViolation<Pendencia>> violations = validator.validate(pendencia);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("O valor deve ser 'S' para resolvida ou 'N' para não resolvida");
    }

    @Test
    @DisplayName("Deve conseguir marcar pendência como resolvida")
    void deveConseguirMarcarPendenciaComoResolvida() {
        // Arrange
        Conformidade conformidade = Conformidade.builder()
                .id(1L)
                .build();

        Pendencia pendencia = Pendencia.builder()
                .conformidade(conformidade)
                .descricaoPendencia("Separar resíduos químicos e orgânicos corretamente")
                .prazoResolucao(LocalDate.now().plusDays(15))
                .resolvida("N")
                .build();

        // Act
        pendencia.setResolvida("S");

        // Assert
        assertThat(pendencia.getResolvida()).isEqualTo("S");
    }
}
