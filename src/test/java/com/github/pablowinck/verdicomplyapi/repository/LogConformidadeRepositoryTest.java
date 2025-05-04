package com.github.pablowinck.verdicomplyapi.repository;

import com.github.pablowinck.verdicomplyapi.model.*;
import org.junit.jupiter.api.BeforeEach;
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
class LogConformidadeRepositoryTest {

    @Autowired
    private LogConformidadeRepository logConformidadeRepository;

    @Autowired
    private ConformidadeRepository conformidadeRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private NormaAmbientalRepository normaAmbientalRepository;

    private Conformidade conformidade;

    @BeforeEach
    void setUp() {
        // Criando departamento
        Departamento departamento = Departamento.builder()
                .nomeDepartamento("Manufatura")
                .build();
        departamentoRepository.save(departamento);

        // Criando auditoria
        Auditoria auditoria = Auditoria.builder()
                .departamento(departamento)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Carlos Silva")
                .statusAuditoria("PENDENTE")
                .build();
        auditoriaRepository.save(auditoria);

        // Criando norma ambiental
        NormaAmbiental norma = NormaAmbiental.builder()
                .codigoNorma("N001")
                .descricao("Emissão de CO2 controlada")
                .orgaoFiscalizador("IBAMA")
                .build();
        normaAmbientalRepository.save(norma);

        // Criando conformidade
        conformidade = Conformidade.builder()
                .auditoria(auditoria)
                .norma(norma)
                .estaConforme("N")
                .observacao("Filtros de emissão com problemas")
                .build();
        conformidadeRepository.save(conformidade);
    }

    @Test
    @DisplayName("Deve salvar log de conformidade com sucesso")
    void deveSalvarLogConformidadeComSucesso() {
        // Arrange
        LogConformidade logConformidade = LogConformidade.builder()
                .conformidade(conformidade)
                .acao("INSERÇÃO")
                .dataRegistro(LocalDate.now())
                .detalhes("Conformidade: N, Obs: Filtros de emissão com problemas")
                .build();

        // Act
        LogConformidade logSalvo = logConformidadeRepository.save(logConformidade);

        // Assert
        assertThat(logSalvo.getId()).isNotNull();
        assertThat(logSalvo.getConformidade().getId()).isEqualTo(conformidade.getId());
        assertThat(logSalvo.getAcao()).isEqualTo("INSERÇÃO");
        assertThat(logSalvo.getDetalhes()).contains("Filtros de emissão com problemas");
    }

    @Test
    @DisplayName("Deve buscar logs por conformidade")
    void deveBuscarLogsPorConformidade() {
        // Arrange
        LogConformidade log1 = LogConformidade.builder()
                .conformidade(conformidade)
                .acao("INSERÇÃO")
                .dataRegistro(LocalDate.now().minusDays(1))
                .detalhes("Conformidade: N, Obs: Filtros de emissão com problemas")
                .build();

        LogConformidade log2 = LogConformidade.builder()
                .conformidade(conformidade)
                .acao("ALTERAÇÃO")
                .dataRegistro(LocalDate.now())
                .detalhes("Conformidade: N, Obs: Filtros substituídos, aguardando verificação")
                .build();

        logConformidadeRepository.saveAll(List.of(log1, log2));

        // Act
        List<LogConformidade> logs = logConformidadeRepository.findByConformidadeOrderByDataRegistroDesc(conformidade);

        // Assert
        assertThat(logs).hasSize(2);
        assertThat(logs.get(0).getAcao()).isEqualTo("ALTERAÇÃO");  // O mais recente primeiro
        assertThat(logs.get(1).getAcao()).isEqualTo("INSERÇÃO");   // O mais antigo depois
    }

    @Test
    @DisplayName("Deve buscar logs por ação")
    void deveBuscarLogsPorAcao() {
        // Arrange
        LogConformidade log1 = LogConformidade.builder()
                .conformidade(conformidade)
                .acao("INSERÇÃO")
                .dataRegistro(LocalDate.now().minusDays(1))
                .detalhes("Conformidade: N, Obs: Filtros de emissão com problemas")
                .build();

        LogConformidade log2 = LogConformidade.builder()
                .conformidade(conformidade)
                .acao("ALTERAÇÃO")
                .dataRegistro(LocalDate.now())
                .detalhes("Conformidade: N, Obs: Filtros substituídos, aguardando verificação")
                .build();

        logConformidadeRepository.saveAll(List.of(log1, log2));

        // Act
        List<LogConformidade> logs = logConformidadeRepository.findByAcao("INSERÇÃO");

        // Assert
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getAcao()).isEqualTo("INSERÇÃO");
    }
}
