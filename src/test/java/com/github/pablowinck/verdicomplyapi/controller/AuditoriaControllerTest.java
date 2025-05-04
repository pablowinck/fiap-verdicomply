package com.github.pablowinck.verdicomplyapi.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pablowinck.verdicomplyapi.config.TestSecurityConfig;
import com.github.pablowinck.verdicomplyapi.dto.AuditoriaDTO;
import com.github.pablowinck.verdicomplyapi.dto.DepartamentoDTO;
import com.github.pablowinck.verdicomplyapi.service.AuditoriaService;
import com.github.pablowinck.verdicomplyapi.service.exception.RecursoNaoEncontradoException;

@WebMvcTest(AuditoriaController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuditoriaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private AuditoriaService auditoriaService;

        @Test
        @WithMockUser(roles = "AUDITOR")
        @DisplayName("Deve listar todas as auditorias")
        void deveListarTodasAsAuditorias() throws Exception {
                // Arrange
                AuditoriaDTO auditoria1 = AuditoriaDTO.builder()
                                .id(1L)
                                .departamentoId(1L)
                                .dataAuditoria(LocalDate.now())
                                .auditorResponsavel("Carlos Silva")
                                .statusAuditoria("PENDENTE")
                                .departamento(DepartamentoDTO.builder().id(1L).nomeDepartamento("Manufatura").build())
                                .build();

                AuditoriaDTO auditoria2 = AuditoriaDTO.builder()
                                .id(2L)
                                .departamentoId(2L)
                                .dataAuditoria(LocalDate.now())
                                .auditorResponsavel("Mariana Costa")
                                .statusAuditoria("CONCLUÍDA")
                                .departamento(DepartamentoDTO.builder().id(2L).nomeDepartamento("Logística").build())
                                .build();

                List<AuditoriaDTO> auditorias = Arrays.asList(auditoria1, auditoria2);

                when(auditoriaService.listarTodas()).thenReturn(auditorias);

                // Act & Assert
                mockMvc.perform(get("/api/auditorias")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].id", is(1)))
                                .andExpect(jsonPath("$[0].auditorResponsavel", is("Carlos Silva")))
                                .andExpect(jsonPath("$[1].id", is(2)))
                                .andExpect(jsonPath("$[1].auditorResponsavel", is("Mariana Costa")));

                verify(auditoriaService, times(1)).listarTodas();
        }

        @Test
        @WithMockUser(roles = "AUDITOR")
        @DisplayName("Deve buscar auditoria por ID")
        void deveBuscarAuditoriaPorId() throws Exception {
                // Arrange
                Long id = 1L;
                AuditoriaDTO auditoria = AuditoriaDTO.builder()
                                .id(id)
                                .departamentoId(1L)
                                .dataAuditoria(LocalDate.now())
                                .auditorResponsavel("Carlos Silva")
                                .statusAuditoria("PENDENTE")
                                .departamento(DepartamentoDTO.builder().id(1L).nomeDepartamento("Manufatura").build())
                                .build();

                when(auditoriaService.buscarPorId(id)).thenReturn(auditoria);

                // Act & Assert
                mockMvc.perform(get("/api/auditorias/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.auditorResponsavel", is("Carlos Silva")));

                verify(auditoriaService, times(1)).buscarPorId(id);
        }

        @Test
        @WithMockUser(roles = "AUDITOR")
        @DisplayName("Deve retornar 404 ao buscar auditoria inexistente")
        void deveRetornar404AoBuscarAuditoriaInexistente() throws Exception {
                // Arrange
                Long id = 999L;
                when(auditoriaService.buscarPorId(id))
                                .thenThrow(new RecursoNaoEncontradoException("Auditoria não encontrada"));

                // Act & Assert
                mockMvc.perform(get("/api/auditorias/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(auditoriaService, times(1)).buscarPorId(id);
        }

        @Test
        @WithMockUser(roles = "GESTOR")
        @DisplayName("Deve criar uma nova auditoria")
        void deveCriarNovaAuditoria() throws Exception {
                // Arrange
                AuditoriaDTO auditoriaDTO = AuditoriaDTO.builder()
                                .departamentoId(1L)
                                .dataAuditoria(LocalDate.now())
                                .auditorResponsavel("Carlos Silva")
                                .statusAuditoria("PENDENTE")
                                .build();

                AuditoriaDTO auditoriaCriada = AuditoriaDTO.builder()
                                .id(1L)
                                .departamentoId(1L)
                                .dataAuditoria(auditoriaDTO.getDataAuditoria())
                                .auditorResponsavel(auditoriaDTO.getAuditorResponsavel())
                                .statusAuditoria(auditoriaDTO.getStatusAuditoria())
                                .departamento(DepartamentoDTO.builder().id(1L).nomeDepartamento("Manufatura").build())
                                .build();

                when(auditoriaService.criar(any(AuditoriaDTO.class))).thenReturn(auditoriaCriada);

                // Act & Assert
                mockMvc.perform(post("/api/auditorias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(auditoriaDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.auditorResponsavel", is("Carlos Silva")));

                verify(auditoriaService, times(1)).criar(any(AuditoriaDTO.class));
        }

        @Test
        @WithMockUser(roles = "GESTOR")
        @DisplayName("Deve atualizar uma auditoria existente")
        void deveAtualizarAuditoriaExistente() throws Exception {
                // Arrange
                Long id = 1L;
                AuditoriaDTO auditoriaDTO = AuditoriaDTO.builder()
                                .departamentoId(2L)
                                .dataAuditoria(LocalDate.now())
                                .auditorResponsavel("Mariana Costa")
                                .statusAuditoria("CONCLUÍDA")
                                .build();

                AuditoriaDTO auditoriaAtualizada = AuditoriaDTO.builder()
                                .id(id)
                                .departamentoId(2L)
                                .dataAuditoria(auditoriaDTO.getDataAuditoria())
                                .auditorResponsavel(auditoriaDTO.getAuditorResponsavel())
                                .statusAuditoria(auditoriaDTO.getStatusAuditoria())
                                .departamento(DepartamentoDTO.builder().id(2L).nomeDepartamento("Logística").build())
                                .build();

                when(auditoriaService.atualizar(eq(id), any(AuditoriaDTO.class))).thenReturn(auditoriaAtualizada);

                // Act & Assert
                mockMvc.perform(put("/api/auditorias/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(auditoriaDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.auditorResponsavel", is("Mariana Costa")))
                                .andExpect(jsonPath("$.statusAuditoria", is("CONCLUÍDA")));

                verify(auditoriaService, times(1)).atualizar(eq(id), any(AuditoriaDTO.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Deve excluir uma auditoria")
        void deveExcluirAuditoria() throws Exception {
                // Arrange
                Long id = 1L;
                doNothing().when(auditoriaService).excluir(id);

                // Act & Assert
                mockMvc.perform(delete("/api/auditorias/{id}", id))
                                .andExpect(status().isNoContent());

                verify(auditoriaService, times(1)).excluir(id);
        }

        @Test
        @WithMockUser(roles = "AUDITOR")
        @DisplayName("Deve buscar auditorias por status")
        void deveBuscarAuditoriasPorStatus() throws Exception {
                // Arrange
                String status = "PENDENTE";
                AuditoriaDTO auditoria1 = AuditoriaDTO.builder()
                                .id(1L)
                                .departamentoId(1L)
                                .dataAuditoria(LocalDate.now())
                                .auditorResponsavel("Carlos Silva")
                                .statusAuditoria(status)
                                .departamento(DepartamentoDTO.builder().id(1L).nomeDepartamento("Manufatura").build())
                                .build();

                AuditoriaDTO auditoria2 = AuditoriaDTO.builder()
                                .id(3L)
                                .departamentoId(3L)
                                .dataAuditoria(LocalDate.now())
                                .auditorResponsavel("Roberto Pereira")
                                .statusAuditoria(status)
                                .departamento(DepartamentoDTO.builder().id(3L).nomeDepartamento("TI Sustentável")
                                                .build())
                                .build();

                List<AuditoriaDTO> auditorias = Arrays.asList(auditoria1, auditoria2);

                when(auditoriaService.buscarPorStatus(status)).thenReturn(auditorias);

                // Act & Assert
                mockMvc.perform(get("/api/auditorias/status/{status}", status)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].id", is(1)))
                                .andExpect(jsonPath("$[0].statusAuditoria", is(status)))
                                .andExpect(jsonPath("$[1].id", is(3)))
                                .andExpect(jsonPath("$[1].statusAuditoria", is(status)));

                verify(auditoriaService, times(1)).buscarPorStatus(status);
        }

        @Test
        @WithMockUser(roles = "AUDITOR")
        @DisplayName("Deve buscar auditorias por departamento")
        void deveBuscarAuditoriasPorDepartamento() throws Exception {
                // Arrange
                Long departamentoId = 1L;
                AuditoriaDTO auditoria1 = AuditoriaDTO.builder()
                                .id(1L)
                                .departamentoId(departamentoId)
                                .dataAuditoria(LocalDate.now().minusDays(5))
                                .auditorResponsavel("Carlos Silva")
                                .statusAuditoria("CONCLUÍDA")
                                .departamento(DepartamentoDTO.builder().id(departamentoId)
                                                .nomeDepartamento("Manufatura").build())
                                .build();

                AuditoriaDTO auditoria2 = AuditoriaDTO.builder()
                                .id(5L)
                                .departamentoId(departamentoId)
                                .dataAuditoria(LocalDate.now())
                                .auditorResponsavel("Roberto Pereira")
                                .statusAuditoria("PENDENTE")
                                .departamento(DepartamentoDTO.builder().id(departamentoId)
                                                .nomeDepartamento("Manufatura").build())
                                .build();

                List<AuditoriaDTO> auditorias = Arrays.asList(auditoria1, auditoria2);

                when(auditoriaService.buscarPorDepartamento(departamentoId)).thenReturn(auditorias);

                // Act & Assert
                mockMvc.perform(get("/api/auditorias/departamento/{departamentoId}", departamentoId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].id", is(1)))
                                .andExpect(jsonPath("$[0].departamentoId", is(departamentoId.intValue())))
                                .andExpect(jsonPath("$[1].id", is(5)))
                                .andExpect(jsonPath("$[1].departamentoId", is(departamentoId.intValue())));

                verify(auditoriaService, times(1)).buscarPorDepartamento(departamentoId);
        }
}
