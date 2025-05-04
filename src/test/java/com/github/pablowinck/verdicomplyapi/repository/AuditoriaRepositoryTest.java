package com.github.pablowinck.verdicomplyapi.repository;

import com.github.pablowinck.verdicomplyapi.model.Auditoria;
import com.github.pablowinck.verdicomplyapi.model.Departamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AuditoriaRepositoryTest {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Test
    @DisplayName("Deve salvar auditoria com sucesso")
    void deveSalvarAuditoriaComSucesso() {
        // Arrange
        Departamento departamento = Departamento.builder()
                .nomeDepartamento("Manufatura")
                .build();
        departamentoRepository.save(departamento);

        Auditoria auditoria = Auditoria.builder()
                .departamento(departamento)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Carlos Silva")
                .statusAuditoria("PENDENTE")
                .build();

        // Act
        Auditoria auditoriaSalva = auditoriaRepository.save(auditoria);

        // Assert
        assertThat(auditoriaSalva.getId()).isNotNull();
        assertThat(auditoriaSalva.getDepartamento().getId()).isEqualTo(departamento.getId());
        assertThat(auditoriaSalva.getAuditorResponsavel()).isEqualTo("Carlos Silva");
        assertThat(auditoriaSalva.getStatusAuditoria()).isEqualTo("PENDENTE");
    }

    @Test
    @DisplayName("Deve buscar auditorias por status")
    void deveBuscarAuditoriasPorStatus() {
        // Arrange
        Departamento departamento = Departamento.builder()
                .nomeDepartamento("Manufatura")
                .build();
        departamentoRepository.save(departamento);

        Auditoria auditoria1 = Auditoria.builder()
                .departamento(departamento)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Carlos Silva")
                .statusAuditoria("PENDENTE")
                .build();

        Auditoria auditoria2 = Auditoria.builder()
                .departamento(departamento)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Mariana Costa")
                .statusAuditoria("CONCLUÍDA")
                .build();

        Auditoria auditoria3 = Auditoria.builder()
                .departamento(departamento)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Roberto Pereira")
                .statusAuditoria("CONCLUÍDA")
                .build();

        auditoriaRepository.saveAll(List.of(auditoria1, auditoria2, auditoria3));

        // Act
        List<Auditoria> auditoriasConcluidas = auditoriaRepository.findByStatusAuditoria("CONCLUÍDA");

        // Assert
        assertThat(auditoriasConcluidas).hasSize(2);
        assertThat(auditoriasConcluidas).extracting(Auditoria::getStatusAuditoria)
                .containsOnly("CONCLUÍDA");
    }

    @Test
    @DisplayName("Deve buscar auditorias por departamento")
    void deveBuscarAuditoriasPorDepartamento() {
        // Arrange
        Departamento departamento1 = Departamento.builder()
                .nomeDepartamento("Manufatura")
                .build();
        Departamento departamento2 = Departamento.builder()
                .nomeDepartamento("Logística")
                .build();
        departamentoRepository.saveAll(List.of(departamento1, departamento2));

        Auditoria auditoria1 = Auditoria.builder()
                .departamento(departamento1)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Carlos Silva")
                .statusAuditoria("PENDENTE")
                .build();

        Auditoria auditoria2 = Auditoria.builder()
                .departamento(departamento2)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Mariana Costa")
                .statusAuditoria("CONCLUÍDA")
                .build();

        Auditoria auditoria3 = Auditoria.builder()
                .departamento(departamento1)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Roberto Pereira")
                .statusAuditoria("CONCLUÍDA")
                .build();

        auditoriaRepository.saveAll(List.of(auditoria1, auditoria2, auditoria3));

        // Act
        List<Auditoria> auditoriasManufatura = auditoriaRepository.findByDepartamento(departamento1);

        // Assert
        assertThat(auditoriasManufatura).hasSize(2);
        assertThat(auditoriasManufatura).extracting(audit -> audit.getDepartamento().getId())
                .containsOnly(departamento1.getId());
    }
}
