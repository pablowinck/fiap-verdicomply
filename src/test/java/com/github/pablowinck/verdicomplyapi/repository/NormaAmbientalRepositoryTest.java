package com.github.pablowinck.verdicomplyapi.repository;

import com.github.pablowinck.verdicomplyapi.model.NormaAmbiental;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class NormaAmbientalRepositoryTest {

    @Autowired
    private NormaAmbientalRepository repository;

    @Test
    @DisplayName("Deve salvar uma norma ambiental com sucesso")
    void deveSalvarNormaAmbientalComSucesso() {
        // Arrange
        NormaAmbiental norma = NormaAmbiental.builder()
                .codigoNorma("N001")
                .descricao("Emissão de CO2 controlada")
                .orgaoFiscalizador("IBAMA")
                .build();

        // Act
        NormaAmbiental normaSalva = repository.save(norma);

        // Assert
        assertThat(normaSalva.getId()).isNotNull();
        assertThat(normaSalva.getCodigoNorma()).isEqualTo("N001");
        assertThat(normaSalva.getDescricao()).isEqualTo("Emissão de CO2 controlada");
        assertThat(normaSalva.getOrgaoFiscalizador()).isEqualTo("IBAMA");
    }

    @Test
    @DisplayName("Deve buscar norma ambiental por código")
    void deveBuscarNormaAmbientalPorCodigo() {
        // Arrange
        NormaAmbiental norma = NormaAmbiental.builder()
                .codigoNorma("N001")
                .descricao("Emissão de CO2 controlada")
                .orgaoFiscalizador("IBAMA")
                .build();
        repository.save(norma);

        // Act
        Optional<NormaAmbiental> resultado = repository.findByCodigoNorma("N001");

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCodigoNorma()).isEqualTo("N001");
    }

    @Test
    @DisplayName("Deve buscar normas ambientais por órgão fiscalizador")
    void deveBuscarNormasAmbientaisPorOrgaoFiscalizador() {
        // Arrange
        NormaAmbiental norma1 = NormaAmbiental.builder()
                .codigoNorma("N001")
                .descricao("Emissão de CO2 controlada")
                .orgaoFiscalizador("IBAMA")
                .build();
        
        NormaAmbiental norma2 = NormaAmbiental.builder()
                .codigoNorma("N004")
                .descricao("Tratamento de efluentes")
                .orgaoFiscalizador("IBAMA")
                .build();
        
        NormaAmbiental norma3 = NormaAmbiental.builder()
                .codigoNorma("N002")
                .descricao("Descarte correto de resíduos químicos")
                .orgaoFiscalizador("CETESB")
                .build();
        
        repository.saveAll(List.of(norma1, norma2, norma3));

        // Act
        List<NormaAmbiental> resultado = repository.findByOrgaoFiscalizador("IBAMA");

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(NormaAmbiental::getOrgaoFiscalizador)
                .containsOnly("IBAMA");
    }
}
