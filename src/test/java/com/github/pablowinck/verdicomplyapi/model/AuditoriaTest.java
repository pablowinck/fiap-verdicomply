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

class AuditoriaTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Deve criar uma auditoria válida")
    void deveCriarAuditoriaValida() {
        // Arrange
        Departamento departamento = Departamento.builder()
                .id(1L)
                .nomeDepartamento("Manufatura")
                .build();

        Auditoria auditoria = Auditoria.builder()
                .departamento(departamento)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Carlos Silva")
                .statusAuditoria("PENDENTE")
                .build();

        // Act
        Set<ConstraintViolation<Auditoria>> violations = validator.validate(auditoria);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(auditoria.getDepartamento()).isEqualTo(departamento);
        assertThat(auditoria.getAuditorResponsavel()).isEqualTo("Carlos Silva");
        assertThat(auditoria.getStatusAuditoria()).isEqualTo("PENDENTE");
    }

    @Test
    @DisplayName("Deve falhar ao criar uma auditoria sem departamento")
    void deveFalharAoCriarAuditoriaSemDepartamento() {
        // Arrange
        Auditoria auditoria = Auditoria.builder()
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Carlos Silva")
                .statusAuditoria("PENDENTE")
                .build();

        // Act
        Set<ConstraintViolation<Auditoria>> violations = validator.validate(auditoria);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("não deve ser nulo");
    }

    @Test
    @DisplayName("Deve conseguir atualizar status da auditoria")
    void deveConseguirAtualizarStatusDaAuditoria() {
        // Arrange
        Departamento departamento = Departamento.builder()
                .id(1L)
                .nomeDepartamento("Manufatura")
                .build();

        Auditoria auditoria = Auditoria.builder()
                .departamento(departamento)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Carlos Silva")
                .statusAuditoria("PENDENTE")
                .build();

        // Act
        auditoria.setStatusAuditoria("CONCLUÍDA");

        // Assert
        assertThat(auditoria.getStatusAuditoria()).isEqualTo("CONCLUÍDA");
    }
}
