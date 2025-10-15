package com.github.pablowinck.verdicomplyapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pablowinck.verdicomplyapi.config.TestSecurityConfig;
import com.github.pablowinck.verdicomplyapi.dto.ConformidadeDTO;
import com.github.pablowinck.verdicomplyapi.dto.LogConformidadeDTO;
import com.github.pablowinck.verdicomplyapi.service.LogConformidadeService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogConformidadeController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class LogConformidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LogConformidadeService logConformidadeService;

    @Test
    @WithMockUser(roles = "GESTOR")
    @DisplayName("Deve listar todos os logs de conformidade")
    void deveListarTodosOsLogs() throws Exception {
        // Arrange
        LocalDate dataHora = LocalDate.now();
        
        LogConformidadeDTO log1 = LogConformidadeDTO.builder()
                .id(1L)
                .conformidadeId(1L)
                .acao("CRIAR")
                .dataRegistro(dataHora)
                .detalhes("Criação de conformidade")
                .build();

        LogConformidadeDTO log2 = LogConformidadeDTO.builder()
                .id(2L)
                .conformidadeId(1L)
                .acao("ATUALIZAR")
                .dataRegistro(dataHora.plusDays(1))
                .detalhes("Atualização de status de conformidade")
                .build();

        List<LogConformidadeDTO> logs = Arrays.asList(log1, log2);

        when(logConformidadeService.listarTodos()).thenReturn(logs);

        // Act & Assert
        mockMvc.perform(get("/api/logs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].acao", is("CRIAR")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].acao", is("ATUALIZAR")));

        verify(logConformidadeService, times(1)).listarTodos();
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @DisplayName("Deve buscar log de conformidade por ID")
    void deveBuscarLogPorId() throws Exception {
        // Arrange
        Long id = 1L;
        LocalDate dataHora = LocalDate.now();
        
        LogConformidadeDTO log = LogConformidadeDTO.builder()
                .id(id)
                .conformidadeId(1L)
                .acao("CRIAR")
                .dataRegistro(dataHora)
                .detalhes("Criação de conformidade")
                .conformidade(ConformidadeDTO.builder().id(1L).build())
                .build();

        when(logConformidadeService.buscarPorId(id)).thenReturn(log);

        // Act & Assert
        mockMvc.perform(get("/api/logs/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.acao", is("CRIAR")));

        verify(logConformidadeService, times(1)).buscarPorId(id);
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @DisplayName("Deve retornar 404 ao buscar log inexistente")
    void deveRetornar404AoBuscarLogInexistente() throws Exception {
        // Arrange
        Long id = 999L;
        when(logConformidadeService.buscarPorId(id)).thenThrow(new RecursoNaoEncontradoException("Log de conformidade não encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api/logs/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(logConformidadeService, times(1)).buscarPorId(id);
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve criar um novo log de conformidade")
    void deveCriarNovoLog() throws Exception {
        // Arrange
        LocalDate dataHora = LocalDate.now();
        
        LogConformidadeDTO logDTO = LogConformidadeDTO.builder()
                .conformidadeId(1L)
                .acao("CRIAR")
                .dataRegistro(dataHora)
                .detalhes("Criação de conformidade")
                .build();

        LogConformidadeDTO logCriado = LogConformidadeDTO.builder()
                .id(1L)
                .conformidadeId(1L)
                .acao("CRIAR")
                .dataRegistro(dataHora)
                .detalhes("Criação de conformidade")
                .conformidade(ConformidadeDTO.builder().id(1L).build())
                .build();

        when(logConformidadeService.criar(any(LogConformidadeDTO.class))).thenReturn(logCriado);

        // Act & Assert
        mockMvc.perform(post("/api/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.acao", is("CRIAR")));

        verify(logConformidadeService, times(1)).criar(any(LogConformidadeDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve excluir um log de conformidade")
    void deveExcluirLog() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(logConformidadeService).excluir(id);

        // Act & Assert
        mockMvc.perform(delete("/api/logs/{id}", id))
                .andExpect(status().isNoContent());

        verify(logConformidadeService, times(1)).excluir(id);
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve buscar logs por conformidade")
    void deveBuscarLogsPorConformidade() throws Exception {
        // Arrange
        Long conformidadeId = 1L;
        LocalDate dataHora = LocalDate.now();
        
        LogConformidadeDTO log1 = LogConformidadeDTO.builder()
                .id(1L)
                .conformidadeId(conformidadeId)
                .acao("CRIAR")
                .dataRegistro(dataHora)
                .detalhes("Criação de conformidade")
                .build();

        LogConformidadeDTO log2 = LogConformidadeDTO.builder()
                .id(2L)
                .conformidadeId(conformidadeId)
                .acao("ATUALIZAR")
                .dataRegistro(dataHora.plusDays(1))
                .detalhes("Atualização de status de conformidade")
                .build();

        List<LogConformidadeDTO> logs = Arrays.asList(log1, log2);

        when(logConformidadeService.buscarPorConformidade(conformidadeId)).thenReturn(logs);

        // Act & Assert
        mockMvc.perform(get("/api/logs/conformidade/{conformidadeId}", conformidadeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].conformidadeId", is(conformidadeId.intValue())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].conformidadeId", is(conformidadeId.intValue())));

        verify(logConformidadeService, times(1)).buscarPorConformidade(conformidadeId);
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @DisplayName("Deve buscar logs por ação")
    void deveBuscarLogsPorAcao() throws Exception {
        // Arrange
        String acao = "CRIAR";
        LocalDate dataHora = LocalDate.now();
        
        LogConformidadeDTO log1 = LogConformidadeDTO.builder()
                .id(1L)
                .conformidadeId(1L)
                .acao(acao)
                .dataRegistro(dataHora)
                .detalhes("Criação de conformidade 1")
                .build();

        LogConformidadeDTO log2 = LogConformidadeDTO.builder()
                .id(3L)
                .conformidadeId(2L)
                .acao(acao)
                .dataRegistro(dataHora.plusDays(1))
                .detalhes("Criação de conformidade 2")
                .build();

        List<LogConformidadeDTO> logs = Arrays.asList(log1, log2);

        when(logConformidadeService.buscarPorAcao(acao)).thenReturn(logs);

        // Act & Assert
        mockMvc.perform(get("/api/logs/acao/{acao}", acao)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].acao", is(acao)))
                .andExpect(jsonPath("$[1].id", is(3)))
                .andExpect(jsonPath("$[1].acao", is(acao)));

        verify(logConformidadeService, times(1)).buscarPorAcao(acao);
    }
}
