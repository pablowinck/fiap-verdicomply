package com.github.pablowinck.verdicomplyapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pablowinck.verdicomplyapi.config.TestSecurityConfig;
import com.github.pablowinck.verdicomplyapi.dto.ConformidadeDTO;
import com.github.pablowinck.verdicomplyapi.dto.PendenciaDTO;
import com.github.pablowinck.verdicomplyapi.service.PendenciaService;
import com.github.pablowinck.verdicomplyapi.service.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PendenciaController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PendenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PendenciaService pendenciaService;

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve listar todas as pendências")
    void deveListarTodasAsPendencias() throws Exception {
        // Arrange
        LocalDate prazoResolucao = LocalDate.now().plusDays(15);
        
        PendenciaDTO pendencia1 = PendenciaDTO.builder()
                .id(1L)
                .conformidadeId(1L)
                .descricao("Instalação de filtros adequados")
                .prazoResolucao(prazoResolucao)
                .resolvida("N")
                .build();

        PendenciaDTO pendencia2 = PendenciaDTO.builder()
                .id(2L)
                .conformidadeId(2L)
                .descricao("Substituição de equipamentos obsoletos")
                .prazoResolucao(prazoResolucao.plusDays(30))
                .resolvida("N")
                .build();

        List<PendenciaDTO> pendencias = Arrays.asList(pendencia1, pendencia2);

        when(pendenciaService.listarTodas()).thenReturn(pendencias);

        // Act & Assert
        mockMvc.perform(get("/api/pendencias")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].descricao", is("Instalação de filtros adequados")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].descricao", is("Substituição de equipamentos obsoletos")));

        verify(pendenciaService, times(1)).listarTodas();
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve buscar pendência por ID")
    void deveBuscarPendenciaPorId() throws Exception {
        // Arrange
        Long id = 1L;
        LocalDate prazoResolucao = LocalDate.now().plusDays(15);
        
        PendenciaDTO pendencia = PendenciaDTO.builder()
                .id(id)
                .conformidadeId(1L)
                .descricao("Instalação de filtros adequados")
                .prazoResolucao(prazoResolucao)
                .resolvida("N")
                .conformidade(ConformidadeDTO.builder().id(1L).build())
                .build();

        when(pendenciaService.buscarPorId(id)).thenReturn(pendencia);

        // Act & Assert
        mockMvc.perform(get("/api/pendencias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.descricao", is("Instalação de filtros adequados")));

        verify(pendenciaService, times(1)).buscarPorId(id);
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve retornar 404 ao buscar pendência inexistente")
    void deveRetornar404AoBuscarPendenciaInexistente() throws Exception {
        // Arrange
        Long id = 999L;
        when(pendenciaService.buscarPorId(id)).thenThrow(new RecursoNaoEncontradoException("Pendência não encontrada"));

        // Act & Assert
        mockMvc.perform(get("/api/pendencias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(pendenciaService, times(1)).buscarPorId(id);
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve criar uma nova pendência")
    void deveCriarNovaPendencia() throws Exception {
        // Arrange
        LocalDate prazoResolucao = LocalDate.now().plusDays(15);
        
        PendenciaDTO pendenciaDTO = PendenciaDTO.builder()
                .conformidadeId(1L)
                .descricao("Instalação de filtros adequados")
                .prazoResolucao(prazoResolucao)
                .resolvida("N")
                .build();

        PendenciaDTO pendenciaCriada = PendenciaDTO.builder()
                .id(1L)
                .conformidadeId(1L)
                .descricao("Instalação de filtros adequados")
                .prazoResolucao(prazoResolucao)
                .resolvida("N")
                .conformidade(ConformidadeDTO.builder().id(1L).build())
                .build();

        when(pendenciaService.criar(any(PendenciaDTO.class))).thenReturn(pendenciaCriada);

        // Act & Assert
        mockMvc.perform(post("/api/pendencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pendenciaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.descricao", is("Instalação de filtros adequados")));

        verify(pendenciaService, times(1)).criar(any(PendenciaDTO.class));
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve atualizar uma pendência existente")
    void deveAtualizarPendenciaExistente() throws Exception {
        // Arrange
        Long id = 1L;
        LocalDate prazoResolucao = LocalDate.now().plusDays(30);
        
        PendenciaDTO pendenciaDTO = PendenciaDTO.builder()
                .conformidadeId(1L)
                .descricao("Instalação de filtros de alta performance")
                .prazoResolucao(prazoResolucao)
                .resolvida("S")
                .dataResolucao(LocalDate.now())
                .observacoes("Instalados filtros conforme especificação")
                .build();

        PendenciaDTO pendenciaAtualizada = PendenciaDTO.builder()
                .id(id)
                .conformidadeId(1L)
                .descricao("Instalação de filtros de alta performance")
                .prazoResolucao(prazoResolucao)
                .resolvida("S")
                .dataResolucao(LocalDate.now())
                .observacoes("Instalados filtros conforme especificação")
                .conformidade(ConformidadeDTO.builder().id(1L).build())
                .build();

        when(pendenciaService.atualizar(eq(id), any(PendenciaDTO.class))).thenReturn(pendenciaAtualizada);

        // Act & Assert
        mockMvc.perform(put("/api/pendencias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pendenciaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.descricao", is("Instalação de filtros de alta performance")))
                .andExpect(jsonPath("$.resolvida", is("S")));

        verify(pendenciaService, times(1)).atualizar(eq(id), any(PendenciaDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve excluir uma pendência")
    void deveExcluirPendencia() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(pendenciaService).excluir(id);

        // Act & Assert
        mockMvc.perform(delete("/api/pendencias/{id}", id))
                .andExpect(status().isNoContent());

        verify(pendenciaService, times(1)).excluir(id);
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve buscar pendências por conformidade")
    void deveBuscarPendenciasPorConformidade() throws Exception {
        // Arrange
        Long conformidadeId = 1L;
        LocalDate prazoResolucao = LocalDate.now().plusDays(15);
        
        PendenciaDTO pendencia1 = PendenciaDTO.builder()
                .id(1L)
                .conformidadeId(conformidadeId)
                .descricao("Instalação de filtros adequados")
                .prazoResolucao(prazoResolucao)
                .resolvida("N")
                .build();

        PendenciaDTO pendencia2 = PendenciaDTO.builder()
                .id(3L)
                .conformidadeId(conformidadeId)
                .descricao("Treinamento da equipe")
                .prazoResolucao(prazoResolucao.plusDays(7))
                .resolvida("N")
                .build();

        List<PendenciaDTO> pendencias = Arrays.asList(pendencia1, pendencia2);

        when(pendenciaService.buscarPorConformidade(conformidadeId)).thenReturn(pendencias);

        // Act & Assert
        mockMvc.perform(get("/api/pendencias/conformidade/{conformidadeId}", conformidadeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].conformidadeId", is(conformidadeId.intValue())))
                .andExpect(jsonPath("$[1].id", is(3)))
                .andExpect(jsonPath("$[1].conformidadeId", is(conformidadeId.intValue())));

        verify(pendenciaService, times(1)).buscarPorConformidade(conformidadeId);
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve buscar pendências por status")
    void deveBuscarPendenciasPorStatus() throws Exception {
        // Arrange
        String status = "N";
        LocalDate prazoResolucao = LocalDate.now().plusDays(15);
        
        PendenciaDTO pendencia1 = PendenciaDTO.builder()
                .id(1L)
                .conformidadeId(1L)
                .descricao("Instalação de filtros adequados")
                .prazoResolucao(prazoResolucao)
                .resolvida(status)
                .build();

        PendenciaDTO pendencia2 = PendenciaDTO.builder()
                .id(3L)
                .conformidadeId(2L)
                .descricao("Treinamento da equipe")
                .prazoResolucao(prazoResolucao.plusDays(7))
                .resolvida(status)
                .build();

        List<PendenciaDTO> pendencias = Arrays.asList(pendencia1, pendencia2);

        when(pendenciaService.buscarPorStatus(status)).thenReturn(pendencias);

        // Act & Assert
        mockMvc.perform(get("/api/pendencias/status/{resolvida}", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].resolvida", is(status)))
                .andExpect(jsonPath("$[1].id", is(3)))
                .andExpect(jsonPath("$[1].resolvida", is(status)));

        verify(pendenciaService, times(1)).buscarPorStatus(status);
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @DisplayName("Deve buscar pendências vencidas")
    void deveBuscarPendenciasVencidas() throws Exception {
        // Arrange
        LocalDate dataReferencia = LocalDate.now();
        
        PendenciaDTO pendencia1 = PendenciaDTO.builder()
                .id(1L)
                .conformidadeId(1L)
                .descricao("Instalação de filtros adequados")
                .prazoResolucao(dataReferencia.minusDays(5))
                .resolvida("N")
                .build();

        PendenciaDTO pendencia2 = PendenciaDTO.builder()
                .id(3L)
                .conformidadeId(2L)
                .descricao("Treinamento da equipe")
                .prazoResolucao(dataReferencia.minusDays(10))
                .resolvida("N")
                .build();

        List<PendenciaDTO> pendencias = Arrays.asList(pendencia1, pendencia2);

        when(pendenciaService.buscarPendenciasVencidas(dataReferencia)).thenReturn(pendencias);

        // Act & Assert
        mockMvc.perform(get("/api/pendencias/vencidas")
                        .param("data", dataReferencia.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(3)));

        verify(pendenciaService, times(1)).buscarPendenciasVencidas(dataReferencia);
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @DisplayName("Deve buscar pendências vencidas com data padrão")
    void deveBuscarPendenciasVencidasComDataPadrao() throws Exception {
        // Arrange
        LocalDate hoje = LocalDate.now();
        
        PendenciaDTO pendencia1 = PendenciaDTO.builder()
                .id(1L)
                .conformidadeId(1L)
                .descricao("Instalação de filtros adequados")
                .prazoResolucao(hoje.minusDays(5))
                .resolvida("N")
                .build();

        List<PendenciaDTO> pendencias = Arrays.asList(pendencia1);

        when(pendenciaService.buscarPendenciasVencidas(any(LocalDate.class))).thenReturn(pendencias);

        // Act & Assert
        mockMvc.perform(get("/api/pendencias/vencidas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(pendenciaService, times(1)).buscarPendenciasVencidas(any(LocalDate.class));
    }
}
