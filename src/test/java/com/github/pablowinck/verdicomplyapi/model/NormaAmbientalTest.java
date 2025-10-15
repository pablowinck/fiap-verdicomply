package com.github.pablowinck.verdicomplyapi.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class NormaAmbientalTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Deve criar uma norma ambiental válida")
    void deveCriarNormaAmbientalValida() {
        // Arrange
        NormaAmbiental norma = NormaAmbiental.builder()
                .codigoNorma("N001")
                .descricao("Emissão de CO2 controlada")
                .orgaoFiscalizador("IBAMA")
                .build();

        // Act
        Set<ConstraintViolation<NormaAmbiental>> violations = validator.validate(norma);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(norma.getCodigoNorma()).isEqualTo("N001");
        assertThat(norma.getDescricao()).isEqualTo("Emissão de CO2 controlada");
        assertThat(norma.getOrgaoFiscalizador()).isEqualTo("IBAMA");
    }

    @Test
    @DisplayName("Deve falhar ao criar uma norma ambiental sem código")
    void deveFalharAoCriarNormaAmbientalSemCodigo() {
        // Arrange
        NormaAmbiental norma = NormaAmbiental.builder()
                .descricao("Emissão de CO2 controlada")
                .orgaoFiscalizador("IBAMA")
                .build();

        // Act
        Set<ConstraintViolation<NormaAmbiental>> violations = validator.validate(norma);

        // Assert
        assertThat(violations).hasSize(1);
        String message = violations.iterator().next().getMessage();
        assertThat(message).matches(".*((must not be null)|(não deve ser nulo)).*");
    }

    @Test
    @DisplayName("Deve conseguir atualizar dados da norma ambiental")
    void deveConseguirAtualizarDadosNormaAmbiental() {
        // Arrange
        NormaAmbiental norma = NormaAmbiental.builder()
                .codigoNorma("N001")
                .descricao("Emissão de CO2 controlada")
                .orgaoFiscalizador("IBAMA")
                .build();

        // Act
        norma.setDescricao("Nova descrição");
        norma.setOrgaoFiscalizador("CETESB");

        // Assert
        assertThat(norma.getDescricao()).isEqualTo("Nova descrição");
        assertThat(norma.getOrgaoFiscalizador()).isEqualTo("CETESB");
    }
}
