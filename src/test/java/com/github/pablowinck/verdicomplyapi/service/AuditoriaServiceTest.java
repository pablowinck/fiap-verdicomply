package com.github.pablowinck.verdicomplyapi.service;

import com.github.pablowinck.verdicomplyapi.dto.AuditoriaDTO;
import com.github.pablowinck.verdicomplyapi.model.Auditoria;
import com.github.pablowinck.verdicomplyapi.model.Departamento;
import com.github.pablowinck.verdicomplyapi.repository.AuditoriaRepository;
import com.github.pablowinck.verdicomplyapi.repository.DepartamentoRepository;
import com.github.pablowinck.verdicomplyapi.service.exception.RecursoNaoEncontradoException;
import com.github.pablowinck.verdicomplyapi.service.impl.AuditoriaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @Mock
    private DepartamentoRepository departamentoRepository;

    @InjectMocks
    private AuditoriaServiceImpl auditoriaService;

    @Test
    @DisplayName("Deve listar todas as auditorias")
    void deveListarTodasAsAuditorias() {
        // Arrange
        Departamento departamento = Departamento.builder()
                .id(1L)
                .nomeDepartamento("Manufatura")
                .build();
        
        Auditoria auditoria1 = Auditoria.builder()
                .id(1L)
                .departamento(departamento)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Carlos Silva")
                .statusAuditoria("PENDENTE")
                .build();
        
        Auditoria auditoria2 = Auditoria.builder()
                .id(2L)
                .departamento(departamento)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Mariana Costa")
                .statusAuditoria("CONCLUÍDA")
                .build();
        
        when(auditoriaRepository.findAll()).thenReturn(Arrays.asList(auditoria1, auditoria2));
        
        // Act
        List<AuditoriaDTO> resultado = auditoriaService.listarTodas();
        
        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(0).getAuditorResponsavel()).isEqualTo("Carlos Silva");
        assertThat(resultado.get(1).getId()).isEqualTo(2L);
        assertThat(resultado.get(1).getAuditorResponsavel()).isEqualTo("Mariana Costa");
        
        verify(auditoriaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar auditoria por ID")
    void deveBuscarAuditoriaPorId() {
        // Arrange
        Long id = 1L;
        Departamento departamento = Departamento.builder()
                .id(1L)
                .nomeDepartamento("Manufatura")
                .build();
        
        Auditoria auditoria = Auditoria.builder()
                .id(id)
                .departamento(departamento)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Carlos Silva")
                .statusAuditoria("PENDENTE")
                .build();
        
        when(auditoriaRepository.findById(id)).thenReturn(Optional.of(auditoria));
        
        // Act
        AuditoriaDTO resultado = auditoriaService.buscarPorId(id);
        
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getAuditorResponsavel()).isEqualTo("Carlos Silva");
        
        verify(auditoriaRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar auditoria com ID inexistente")
    void deveLancarExcecaoAoBuscarAuditoriaComIdInexistente() {
        // Arrange
        Long id = 999L;
        when(auditoriaRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> auditoriaService.buscarPorId(id))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Auditoria não encontrada");
        
        verify(auditoriaRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve criar uma nova auditoria")
    void deveCriarNovaAuditoria() {
        // Arrange
        Departamento departamento = Departamento.builder()
                .id(1L)
                .nomeDepartamento("Manufatura")
                .build();
        
        AuditoriaDTO auditoriaDTO = AuditoriaDTO.builder()
                .departamentoId(1L)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Carlos Silva")
                .statusAuditoria("PENDENTE")
                .build();
        
        Auditoria auditoriaSalva = Auditoria.builder()
                .id(1L)
                .departamento(departamento)
                .dataAuditoria(auditoriaDTO.getDataAuditoria())
                .auditorResponsavel(auditoriaDTO.getAuditorResponsavel())
                .statusAuditoria(auditoriaDTO.getStatusAuditoria())
                .build();
        
        when(departamentoRepository.findById(auditoriaDTO.getDepartamentoId())).thenReturn(Optional.of(departamento));
        when(auditoriaRepository.save(any(Auditoria.class))).thenReturn(auditoriaSalva);
        
        // Act
        AuditoriaDTO resultado = auditoriaService.criar(auditoriaDTO);
        
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getAuditorResponsavel()).isEqualTo("Carlos Silva");
        
        verify(departamentoRepository, times(1)).findById(auditoriaDTO.getDepartamentoId());
        verify(auditoriaRepository, times(1)).save(any(Auditoria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar auditoria com departamento inexistente")
    void deveLancarExcecaoAoCriarAuditoriaComDepartamentoInexistente() {
        // Arrange
        AuditoriaDTO auditoriaDTO = AuditoriaDTO.builder()
                .departamentoId(999L)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Carlos Silva")
                .statusAuditoria("PENDENTE")
                .build();
        
        when(departamentoRepository.findById(auditoriaDTO.getDepartamentoId())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> auditoriaService.criar(auditoriaDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Departamento não encontrado");
        
        verify(departamentoRepository, times(1)).findById(auditoriaDTO.getDepartamentoId());
        verify(auditoriaRepository, never()).save(any(Auditoria.class));
    }

    @Test
    @DisplayName("Deve atualizar uma auditoria existente")
    void deveAtualizarAuditoriaExistente() {
        // Arrange
        Long auditoriaId = 1L;
        Departamento departamento = Departamento.builder()
                .id(1L)
                .nomeDepartamento("Manufatura")
                .build();
        
        Departamento novoDepartamento = Departamento.builder()
                .id(2L)
                .nomeDepartamento("Logística")
                .build();
        
        Auditoria auditoriaExistente = Auditoria.builder()
                .id(auditoriaId)
                .departamento(departamento)
                .dataAuditoria(LocalDate.now().minusDays(1))
                .auditorResponsavel("Carlos Silva")
                .statusAuditoria("PENDENTE")
                .build();
        
        AuditoriaDTO auditoriaDTO = AuditoriaDTO.builder()
                .id(auditoriaId)
                .departamentoId(2L)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Mariana Costa")
                .statusAuditoria("CONCLUÍDA")
                .build();
        
        Auditoria auditoriaAtualizada = Auditoria.builder()
                .id(auditoriaId)
                .departamento(novoDepartamento)
                .dataAuditoria(auditoriaDTO.getDataAuditoria())
                .auditorResponsavel(auditoriaDTO.getAuditorResponsavel())
                .statusAuditoria(auditoriaDTO.getStatusAuditoria())
                .build();
        
        when(auditoriaRepository.findById(auditoriaId)).thenReturn(Optional.of(auditoriaExistente));
        when(departamentoRepository.findById(auditoriaDTO.getDepartamentoId())).thenReturn(Optional.of(novoDepartamento));
        when(auditoriaRepository.save(any(Auditoria.class))).thenReturn(auditoriaAtualizada);
        
        // Act
        AuditoriaDTO resultado = auditoriaService.atualizar(auditoriaId, auditoriaDTO);
        
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(auditoriaId);
        assertThat(resultado.getDepartamentoId()).isEqualTo(2L);
        assertThat(resultado.getAuditorResponsavel()).isEqualTo("Mariana Costa");
        assertThat(resultado.getStatusAuditoria()).isEqualTo("CONCLUÍDA");
        
        verify(auditoriaRepository, times(1)).findById(auditoriaId);
        verify(departamentoRepository, times(1)).findById(auditoriaDTO.getDepartamentoId());
        verify(auditoriaRepository, times(1)).save(any(Auditoria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar auditoria inexistente")
    void deveLancarExcecaoAoAtualizarAuditoriaInexistente() {
        // Arrange
        Long auditoriaId = 999L;
        AuditoriaDTO auditoriaDTO = AuditoriaDTO.builder()
                .id(auditoriaId)
                .departamentoId(1L)
                .dataAuditoria(LocalDate.now())
                .auditorResponsavel("Mariana Costa")
                .statusAuditoria("CONCLUÍDA")
                .build();
        
        when(auditoriaRepository.findById(auditoriaId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> auditoriaService.atualizar(auditoriaId, auditoriaDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Auditoria não encontrada");
        
        verify(auditoriaRepository, times(1)).findById(auditoriaId);
        verify(departamentoRepository, never()).findById(anyLong());
        verify(auditoriaRepository, never()).save(any(Auditoria.class));
    }
}
