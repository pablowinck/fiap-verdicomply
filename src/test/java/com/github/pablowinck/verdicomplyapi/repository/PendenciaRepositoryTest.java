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
class PendenciaRepositoryTest {

    @Autowired
    private PendenciaRepository pendenciaRepository;

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
    @DisplayName("Deve salvar pendência com sucesso")
    void deveSalvarPendenciaComSucesso() {
        // Arrange
        Pendencia pendencia = Pendencia.builder()
                .conformidade(conformidade)
                .descricaoPendencia("Trocar filtros de emissão")
                .prazoResolucao(LocalDate.now().plusDays(15))
                .resolvida("N")
                .build();

        // Act
        Pendencia pendenciaSalva = pendenciaRepository.save(pendencia);

        // Assert
        assertThat(pendenciaSalva.getId()).isNotNull();
        assertThat(pendenciaSalva.getConformidade().getId()).isEqualTo(conformidade.getId());
        assertThat(pendenciaSalva.getDescricaoPendencia()).isEqualTo("Trocar filtros de emissão");
        assertThat(pendenciaSalva.getResolvida()).isEqualTo("N");
    }

    @Test
    @DisplayName("Deve buscar pendências por conformidade")
    void deveBuscarPendenciasPorConformidade() {
        // Arrange
        Pendencia pendencia = Pendencia.builder()
                .conformidade(conformidade)
                .descricaoPendencia("Trocar filtros de emissão")
                .prazoResolucao(LocalDate.now().plusDays(15))
                .resolvida("N")
                .build();
        pendenciaRepository.save(pendencia);

        // Act
        List<Pendencia> pendencias = pendenciaRepository.findByConformidade(conformidade);

        // Assert
        assertThat(pendencias).hasSize(1);
        assertThat(pendencias.get(0).getConformidade().getId()).isEqualTo(conformidade.getId());
    }

    @Test
    @DisplayName("Deve buscar pendências não resolvidas")
    void deveBuscarPendenciasNaoResolvidas() {
        // Arrange
        Pendencia pendencia1 = Pendencia.builder()
                .conformidade(conformidade)
                .descricaoPendencia("Trocar filtros de emissão")
                .prazoResolucao(LocalDate.now().plusDays(15))
                .resolvida("N")
                .build();

        Pendencia pendencia2 = Pendencia.builder()
                .conformidade(conformidade)
                .descricaoPendencia("Atualizar documentação técnica")
                .prazoResolucao(LocalDate.now().plusDays(10))
                .resolvida("S")
                .build();

        pendenciaRepository.saveAll(List.of(pendencia1, pendencia2));

        // Act
        List<Pendencia> pendenciasNaoResolvidas = pendenciaRepository.findByResolvida("N");

        // Assert
        assertThat(pendenciasNaoResolvidas).hasSize(1);
        assertThat(pendenciasNaoResolvidas.get(0).getDescricaoPendencia()).isEqualTo("Trocar filtros de emissão");
        assertThat(pendenciasNaoResolvidas.get(0).getResolvida()).isEqualTo("N");
    }

    @Test
    @DisplayName("Deve buscar pendências com prazo vencido")
    void deveBuscarPendenciaComPrazoVencido() {
        // Arrange
        Pendencia pendencia1 = Pendencia.builder()
                .conformidade(conformidade)
                .descricaoPendencia("Trocar filtros de emissão")
                .prazoResolucao(LocalDate.now().minusDays(5))
                .resolvida("N")
                .build();

        Pendencia pendencia2 = Pendencia.builder()
                .conformidade(conformidade)
                .descricaoPendencia("Atualizar documentação técnica")
                .prazoResolucao(LocalDate.now().plusDays(10))
                .resolvida("N")
                .build();

        pendenciaRepository.saveAll(List.of(pendencia1, pendencia2));

        // Act
        List<Pendencia> pendenciasVencidas = pendenciaRepository.findByResolvidaAndPrazoResolucaoBefore("N", LocalDate.now());

        // Assert
        assertThat(pendenciasVencidas).hasSize(1);
        assertThat(pendenciasVencidas.get(0).getDescricaoPendencia()).isEqualTo("Trocar filtros de emissão");
    }
}
