package com.github.pablowinck.verdicomplyapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pablowinck.verdicomplyapi.config.TestSecurityConfig;
import com.github.pablowinck.verdicomplyapi.dto.AuditoriaDTO;
import com.github.pablowinck.verdicomplyapi.dto.ConformidadeDTO;
import com.github.pablowinck.verdicomplyapi.dto.NormaAmbientalDTO;
import com.github.pablowinck.verdicomplyapi.service.ConformidadeService;
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

@WebMvcTest(ConformidadeController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ConformidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ConformidadeService conformidadeService;

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve listar todas as conformidades")
    void deveListarTodasAsConformidades() throws Exception {
        // Arrange
        ConformidadeDTO conformidade1 = ConformidadeDTO.builder()
                .id(1L)
                .auditoriaId(1L)
                .normaAmbientalId(1L)
                .estaConforme("S")
                .observacoes("Conforme às normas ambientais")
                .build();

        ConformidadeDTO conformidade2 = ConformidadeDTO.builder()
                .id(2L)
                .auditoriaId(1L)
                .normaAmbientalId(2L)
                .estaConforme("N")
                .observacoes("Não conforme às normas ambientais")
                .build();

        List<ConformidadeDTO> conformidades = Arrays.asList(conformidade1, conformidade2);

        when(conformidadeService.listarTodas()).thenReturn(conformidades);

        // Act & Assert
        mockMvc.perform(get("/api/conformidades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].estaConforme", is("S")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].estaConforme", is("N")));

        verify(conformidadeService, times(1)).listarTodas();
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve buscar conformidade por ID")
    void deveBuscarConformidadePorId() throws Exception {
        // Arrange
        Long id = 1L;
        ConformidadeDTO conformidade = ConformidadeDTO.builder()
                .id(id)
                .auditoriaId(1L)
                .normaAmbientalId(1L)
                .estaConforme("S")
                .observacoes("Conforme às normas ambientais")
                .auditoria(AuditoriaDTO.builder().id(1L).build())
                .normaAmbiental(NormaAmbientalDTO.builder().id(1L).build())
                .build();

        when(conformidadeService.buscarPorId(id)).thenReturn(conformidade);

        // Act & Assert
        mockMvc.perform(get("/api/conformidades/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estaConforme", is("S")));

        verify(conformidadeService, times(1)).buscarPorId(id);
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve retornar 404 ao buscar conformidade inexistente")
    void deveRetornar404AoBuscarConformidadeInexistente() throws Exception {
        // Arrange
        Long id = 999L;
        when(conformidadeService.buscarPorId(id)).thenThrow(new RecursoNaoEncontradoException("Conformidade não encontrada"));

        // Act & Assert
        mockMvc.perform(get("/api/conformidades/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(conformidadeService, times(1)).buscarPorId(id);
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve criar uma nova conformidade")
    void deveCriarNovaConformidade() throws Exception {
        // Arrange
        ConformidadeDTO conformidadeDTO = ConformidadeDTO.builder()
                .auditoriaId(1L)
                .normaAmbientalId(1L)
                .estaConforme("S")
                .observacoes("Conforme às normas ambientais")
                .build();

        ConformidadeDTO conformidadeCriada = ConformidadeDTO.builder()
                .id(1L)
                .auditoriaId(1L)
                .normaAmbientalId(1L)
                .estaConforme("S")
                .observacoes("Conforme às normas ambientais")
                .auditoria(AuditoriaDTO.builder().id(1L).build())
                .normaAmbiental(NormaAmbientalDTO.builder().id(1L).build())
                .build();

        when(conformidadeService.criar(any(ConformidadeDTO.class))).thenReturn(conformidadeCriada);

        // Act & Assert
        mockMvc.perform(post("/api/conformidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conformidadeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estaConforme", is("S")));

        verify(conformidadeService, times(1)).criar(any(ConformidadeDTO.class));
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve atualizar uma conformidade existente")
    void deveAtualizarConformidadeExistente() throws Exception {
        // Arrange
        Long id = 1L;
        ConformidadeDTO conformidadeDTO = ConformidadeDTO.builder()
                .auditoriaId(1L)
                .normaAmbientalId(1L)
                .estaConforme("N")
                .observacoes("Não conforme às normas ambientais")
                .build();

        ConformidadeDTO conformidadeAtualizada = ConformidadeDTO.builder()
                .id(id)
                .auditoriaId(1L)
                .normaAmbientalId(1L)
                .estaConforme("N")
                .observacoes("Não conforme às normas ambientais")
                .auditoria(AuditoriaDTO.builder().id(1L).build())
                .normaAmbiental(NormaAmbientalDTO.builder().id(1L).build())
                .build();

        when(conformidadeService.atualizar(eq(id), any(ConformidadeDTO.class))).thenReturn(conformidadeAtualizada);

        // Act & Assert
        mockMvc.perform(put("/api/conformidades/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conformidadeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estaConforme", is("N")));

        verify(conformidadeService, times(1)).atualizar(eq(id), any(ConformidadeDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve excluir uma conformidade")
    void deveExcluirConformidade() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(conformidadeService).excluir(id);

        // Act & Assert
        mockMvc.perform(delete("/api/conformidades/{id}", id))
                .andExpect(status().isNoContent());

        verify(conformidadeService, times(1)).excluir(id);
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve buscar conformidades por auditoria")
    void deveBuscarConformidadesPorAuditoria() throws Exception {
        // Arrange
        Long auditoriaId = 1L;
        ConformidadeDTO conformidade1 = ConformidadeDTO.builder()
                .id(1L)
                .auditoriaId(auditoriaId)
                .normaAmbientalId(1L)
                .estaConforme("S")
                .observacoes("Conforme às normas ambientais")
                .build();

        ConformidadeDTO conformidade2 = ConformidadeDTO.builder()
                .id(2L)
                .auditoriaId(auditoriaId)
                .normaAmbientalId(2L)
                .estaConforme("N")
                .observacoes("Não conforme às normas ambientais")
                .build();

        List<ConformidadeDTO> conformidades = Arrays.asList(conformidade1, conformidade2);

        when(conformidadeService.buscarPorAuditoria(auditoriaId)).thenReturn(conformidades);

        // Act & Assert
        mockMvc.perform(get("/api/conformidades/auditoria/{auditoriaId}", auditoriaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].auditoriaId", is(auditoriaId.intValue())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].auditoriaId", is(auditoriaId.intValue())));

        verify(conformidadeService, times(1)).buscarPorAuditoria(auditoriaId);
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve buscar conformidades por status")
    void deveBuscarConformidadesPorStatus() throws Exception {
        // Arrange
        String status = "S";
        ConformidadeDTO conformidade1 = ConformidadeDTO.builder()
                .id(1L)
                .auditoriaId(1L)
                .normaAmbientalId(1L)
                .estaConforme(status)
                .observacoes("Conforme às normas ambientais")
                .build();

        ConformidadeDTO conformidade2 = ConformidadeDTO.builder()
                .id(3L)
                .auditoriaId(2L)
                .normaAmbientalId(1L)
                .estaConforme(status)
                .observacoes("Conforme às normas ambientais")
                .build();

        List<ConformidadeDTO> conformidades = Arrays.asList(conformidade1, conformidade2);

        when(conformidadeService.buscarPorStatus(status)).thenReturn(conformidades);

        // Act & Assert
        mockMvc.perform(get("/api/conformidades/status/{estaConforme}", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].estaConforme", is(status)))
                .andExpect(jsonPath("$[1].id", is(3)))
                .andExpect(jsonPath("$[1].estaConforme", is(status)));

        verify(conformidadeService, times(1)).buscarPorStatus(status);
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    @DisplayName("Deve buscar conformidades por norma")
    void deveBuscarConformidadesPorNorma() throws Exception {
        // Arrange
        Long normaId = 1L;
        ConformidadeDTO conformidade1 = ConformidadeDTO.builder()
                .id(1L)
                .auditoriaId(1L)
                .normaAmbientalId(normaId)
                .estaConforme("S")
                .observacoes("Conforme às normas ambientais")
                .build();

        ConformidadeDTO conformidade2 = ConformidadeDTO.builder()
                .id(3L)
                .auditoriaId(2L)
                .normaAmbientalId(normaId)
                .estaConforme("N")
                .observacoes("Não conforme às normas ambientais")
                .build();

        List<ConformidadeDTO> conformidades = Arrays.asList(conformidade1, conformidade2);

        when(conformidadeService.buscarPorNorma(normaId)).thenReturn(conformidades);

        // Act & Assert
        mockMvc.perform(get("/api/conformidades/norma/{normaAmbientalId}", normaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].normaAmbientalId", is(normaId.intValue())))
                .andExpect(jsonPath("$[1].id", is(3)))
                .andExpect(jsonPath("$[1].normaAmbientalId", is(normaId.intValue())));

        verify(conformidadeService, times(1)).buscarPorNorma(normaId);
    }
}
