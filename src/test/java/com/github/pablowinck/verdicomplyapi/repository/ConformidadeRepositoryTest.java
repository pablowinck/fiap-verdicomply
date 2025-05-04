package com.github.pablowinck.verdicomplyapi.repository;

import com.github.pablowinck.verdicomplyapi.model.Auditoria;
import com.github.pablowinck.verdicomplyapi.model.Conformidade;
import com.github.pablowinck.verdicomplyapi.model.Departamento;
import com.github.pablowinck.verdicomplyapi.model.NormaAmbiental;
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
class ConformidadeRepositoryTest {

    @Autowired
    private ConformidadeRepository conformidadeRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private NormaAmbientalRepository normaAmbientalRepository;

    private Auditoria auditoria;
    private NormaAmbiental norma;

    @BeforeEach
    void setUp() {
        // Criando departamento
        Departamento departamento = Departamento.builder()
                .nomeDepartamento("Manufatura")
                .build();
        departamentoRepository.save(departamento);

        // Criando auditoria
        auditoria = Auditoria.builder()
                .departamento(departamento)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Carlos Silva")
                .statusAuditoria("PENDENTE")
                .build();
        auditoriaRepository.save(auditoria);

        // Criando norma ambiental
        norma = NormaAmbiental.builder()
                .codigoNorma("N001")
                .descricao("Emissão de CO2 controlada")
                .orgaoFiscalizador("IBAMA")
                .build();
        normaAmbientalRepository.save(norma);
    }

    @Test
    @DisplayName("Deve salvar conformidade com sucesso")
    void deveSalvarConformidadeComSucesso() {
        // Arrange
        Conformidade conformidade = Conformidade.builder()
                .auditoria(auditoria)
                .norma(norma)
                .estaConforme("S")
                .observacao("Todos os filtros estão dentro do padrão")
                .build();

        // Act
        Conformidade conformidadeSalva = conformidadeRepository.save(conformidade);

        // Assert
        assertThat(conformidadeSalva.getId()).isNotNull();
        assertThat(conformidadeSalva.getAuditoria().getId()).isEqualTo(auditoria.getId());
        assertThat(conformidadeSalva.getNorma().getId()).isEqualTo(norma.getId());
        assertThat(conformidadeSalva.getEstaConforme()).isEqualTo("S");
        assertThat(conformidadeSalva.getObservacao()).isEqualTo("Todos os filtros estão dentro do padrão");
    }

    @Test
    @DisplayName("Deve buscar conformidades por auditoria")
    void deveBuscarConformidadesPorAuditoria() {
        // Arrange
        Conformidade conformidade1 = Conformidade.builder()
                .auditoria(auditoria)
                .norma(norma)
                .estaConforme("S")
                .observacao("Todos os filtros estão dentro do padrão")
                .build();

        NormaAmbiental outraNorma = NormaAmbiental.builder()
                .codigoNorma("N002")
                .descricao("Descarte correto de resíduos químicos")
                .orgaoFiscalizador("CETESB")
                .build();
        normaAmbientalRepository.save(outraNorma);

        Conformidade conformidade2 = Conformidade.builder()
                .auditoria(auditoria)
                .norma(outraNorma)
                .estaConforme("N")
                .observacao("Resíduos não separados corretamente")
                .build();

        conformidadeRepository.saveAll(List.of(conformidade1, conformidade2));

        // Act
        List<Conformidade> conformidades = conformidadeRepository.findByAuditoria(auditoria);

        // Assert
        assertThat(conformidades).hasSize(2);
        assertThat(conformidades).extracting(Conformidade::getAuditoria)
                .extracting(Auditoria::getId)
                .containsOnly(auditoria.getId());
    }

    @Test
    @DisplayName("Deve buscar conformidades por status de conformidade")
    void deveBuscarConformidadesPorStatusDeConformidade() {
        // Arrange
        Conformidade conformidade1 = Conformidade.builder()
                .auditoria(auditoria)
                .norma(norma)
                .estaConforme("S")
                .observacao("Todos os filtros estão dentro do padrão")
                .build();

        NormaAmbiental outraNorma = NormaAmbiental.builder()
                .codigoNorma("N002")
                .descricao("Descarte correto de resíduos químicos")
                .orgaoFiscalizador("CETESB")
                .build();
        normaAmbientalRepository.save(outraNorma);

        Conformidade conformidade2 = Conformidade.builder()
                .auditoria(auditoria)
                .norma(outraNorma)
                .estaConforme("N")
                .observacao("Resíduos não separados corretamente")
                .build();

        conformidadeRepository.saveAll(List.of(conformidade1, conformidade2));

        // Act
        List<Conformidade> conformidadesNaoConformes = conformidadeRepository.findByEstaConforme("N");

        // Assert
        assertThat(conformidadesNaoConformes).hasSize(1);
        assertThat(conformidadesNaoConformes.get(0).getEstaConforme()).isEqualTo("N");
        assertThat(conformidadesNaoConformes.get(0).getObservacao()).isEqualTo("Resíduos não separados corretamente");
    }
}
