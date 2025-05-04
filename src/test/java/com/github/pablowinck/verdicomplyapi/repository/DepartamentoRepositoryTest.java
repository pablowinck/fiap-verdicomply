package com.github.pablowinck.verdicomplyapi.repository;

import com.github.pablowinck.verdicomplyapi.model.Departamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DepartamentoRepositoryTest {

    @Autowired
    private DepartamentoRepository repository;

    @Test
    @DisplayName("Deve salvar um departamento com sucesso")
    void deveSalvarDepartamentoComSucesso() {
        // Arrange
        String nomeDepartamentoUnico = "Manufatura_Teste_Unitario_" + System.currentTimeMillis();
        Departamento departamento = Departamento.builder()
                .nomeDepartamento(nomeDepartamentoUnico)
                .build();

        // Act
        Departamento departamentoSalvo = repository.save(departamento);

        // Assert
        assertThat(departamentoSalvo.getId()).isNotNull();
        assertThat(departamentoSalvo.getNomeDepartamento()).isEqualTo(nomeDepartamentoUnico);
    }

    @Test
    @DisplayName("Deve buscar departamento por nome")
    void deveBuscarDepartamentoPorNome() {
        // Arrange
        String nomeDepartamentoUnico = "Departamento_Teste_" + System.currentTimeMillis();
        Departamento departamento = Departamento.builder()
                .nomeDepartamento(nomeDepartamentoUnico)
                .build();
        repository.save(departamento);

        // Act
        Optional<Departamento> resultado = repository.findByNomeDepartamento(nomeDepartamentoUnico);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNomeDepartamento()).isEqualTo(nomeDepartamentoUnico);
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar departamento inexistente")
    void deveRetornarVazioAoBuscarDepartamentoInexistente() {
        // Act
        Optional<Departamento> resultado = repository.findByNomeDepartamento("Inexistente");

        // Assert
        assertThat(resultado).isEmpty();
    }
}
