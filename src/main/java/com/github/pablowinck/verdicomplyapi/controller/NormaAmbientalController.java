package com.github.pablowinck.verdicomplyapi.controller;

import com.github.pablowinck.verdicomplyapi.dto.NormaAmbientalDTO;
import com.github.pablowinck.verdicomplyapi.model.NormaAmbiental;
import com.github.pablowinck.verdicomplyapi.repository.NormaAmbientalRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para operações REST relacionadas a normas ambientais
 */
@RestController
@RequestMapping("/api/normas")
@Slf4j
public class NormaAmbientalController {

    @Autowired
    private NormaAmbientalRepository normaAmbientalRepository;

    /**
     * Lista todas as normas ambientais
     * @return Lista de normas ambientais
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<?> listarNormas() {
        try {
            log.info("Listando todas as normas ambientais");
            List<NormaAmbiental> normas = normaAmbientalRepository.findAll();
            List<NormaAmbientalDTO> normasDTO = new ArrayList<>();
            
            for (NormaAmbiental norma : normas) {
                try {
                    normasDTO.add(converterParaDTO(norma));
                } catch (Exception e) {
                    log.error("Erro ao converter norma para DTO: {}", e.getMessage());
                }
            }
            
            return ResponseEntity.ok(normasDTO);
        } catch (Exception e) {
            log.error("Erro ao listar normas ambientais: {}", e.getMessage(), e);
            Map<String, Object> erro = new HashMap<>();
            erro.put("mensagem", "Erro ao listar normas ambientais");
            erro.put("erro", e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Busca uma norma ambiental pelo ID
     * @param id ID da norma ambiental
     * @return Norma ambiental
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            log.info("Buscando norma ambiental por ID: {}", id);
            return normaAmbientalRepository.findById(id)
                    .map(norma -> {
                        try {
                            return ResponseEntity.ok(converterParaDTO(norma));
                        } catch (Exception e) {
                            log.error("Erro ao converter norma para DTO: {}", e.getMessage());
                            Map<String, Object> erro = new HashMap<>();
                            erro.put("mensagem", "Erro ao converter norma para DTO");
                            erro.put("erro", e.getMessage());
                            return ResponseEntity.status(500).body(erro);
                        }
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar norma ambiental por ID: {}", e.getMessage(), e);
            Map<String, Object> erro = new HashMap<>();
            erro.put("mensagem", "Erro ao buscar norma ambiental");
            erro.put("erro", e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Busca normas ambientais pelo órgão fiscalizador
     * @param orgaoFiscalizador Órgão fiscalizador
     * @return Lista de normas ambientais
     */
    @GetMapping("/orgao/{orgaoFiscalizador}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<?> buscarPorOrgao(@PathVariable String orgaoFiscalizador) {
        try {
            log.info("Buscando normas ambientais por órgão fiscalizador: {}", orgaoFiscalizador);
            List<NormaAmbiental> normas = normaAmbientalRepository.findByOrgaoFiscalizador(orgaoFiscalizador);
            List<NormaAmbientalDTO> normasDTO = new ArrayList<>();
            
            for (NormaAmbiental norma : normas) {
                try {
                    normasDTO.add(converterParaDTO(norma));
                } catch (Exception e) {
                    log.error("Erro ao converter norma para DTO: {}", e.getMessage());
                }
            }
            
            return ResponseEntity.ok(normasDTO);
        } catch (Exception e) {
            log.error("Erro ao buscar normas por órgão fiscalizador: {}", e.getMessage(), e);
            Map<String, Object> erro = new HashMap<>();
            erro.put("mensagem", "Erro ao buscar normas por órgão fiscalizador");
            erro.put("erro", e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Busca uma norma ambiental pelo código
     * @param codigoNorma Código da norma
     * @return Norma ambiental
     */
    @GetMapping("/codigo/{codigoNorma}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigoNorma) {
        try {
            log.info("Buscando norma ambiental por código: {}", codigoNorma);
            return normaAmbientalRepository.findByCodigoNorma(codigoNorma)
                    .map(norma -> {
                        try {
                            return ResponseEntity.ok(converterParaDTO(norma));
                        } catch (Exception e) {
                            log.error("Erro ao converter norma para DTO: {}", e.getMessage());
                            Map<String, Object> erro = new HashMap<>();
                            erro.put("mensagem", "Erro ao converter norma para DTO");
                            erro.put("erro", e.getMessage());
                            return ResponseEntity.status(500).body(erro);
                        }
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar norma ambiental por código: {}", e.getMessage(), e);
            Map<String, Object> erro = new HashMap<>();
            erro.put("mensagem", "Erro ao buscar norma ambiental por código");
            erro.put("erro", e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Cria uma nova norma ambiental
     * @param dto DTO com os dados da norma ambiental
     * @return Norma ambiental criada
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'ADMIN')")
    public ResponseEntity<?> criarNorma(@Valid @RequestBody NormaAmbientalDTO dto) {
        try {
            log.info("Criando nova norma ambiental: {}", dto.getCodigoNorma());
            // Verifica se já existe uma norma com o mesmo código
            if (normaAmbientalRepository.findByCodigoNorma(dto.getCodigoNorma()).isPresent()) {
                Map<String, Object> erro = new HashMap<>();
                erro.put("mensagem", "Já existe uma norma com o mesmo código");
                return ResponseEntity.badRequest().body(erro);
            }
            
            NormaAmbiental norma = converterParaEntidade(dto);
            norma = normaAmbientalRepository.save(norma);
            return ResponseEntity.ok(converterParaDTO(norma));
        } catch (Exception e) {
            log.error("Erro ao criar norma ambiental: {}", e.getMessage(), e);
            Map<String, Object> erro = new HashMap<>();
            erro.put("mensagem", "Erro ao criar norma ambiental");
            erro.put("erro", e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Atualiza uma norma ambiental existente
     * @param id ID da norma ambiental
     * @param dto DTO com os dados atualizados
     * @return Norma ambiental atualizada
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'ADMIN')")
    public ResponseEntity<?> atualizarNorma(@PathVariable Long id, @Valid @RequestBody NormaAmbientalDTO dto) {
        try {
            log.info("Atualizando norma ambiental ID: {}", id);
            if (!normaAmbientalRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            NormaAmbiental norma = converterParaEntidade(dto);
            norma.setId(id);
            norma = normaAmbientalRepository.save(norma);
            return ResponseEntity.ok(converterParaDTO(norma));
        } catch (Exception e) {
            log.error("Erro ao atualizar norma ambiental: {}", e.getMessage(), e);
            Map<String, Object> erro = new HashMap<>();
            erro.put("mensagem", "Erro ao atualizar norma ambiental");
            erro.put("erro", e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Remove uma norma ambiental
     * @param id ID da norma ambiental
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removerNorma(@PathVariable Long id) {
        try {
            log.info("Removendo norma ambiental ID: {}", id);
            if (!normaAmbientalRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            normaAmbientalRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao remover norma ambiental: {}", e.getMessage(), e);
            Map<String, Object> erro = new HashMap<>();
            erro.put("mensagem", "Erro ao remover norma ambiental");
            erro.put("erro", e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Converte uma entidade NormaAmbiental para um DTO
     * @param norma Entidade NormaAmbiental
     * @return DTO NormaAmbientalDTO
     */
    private NormaAmbientalDTO converterParaDTO(NormaAmbiental norma) {
        return NormaAmbientalDTO.builder()
                .id(norma.getId())
                .codigoNorma(norma.getCodigoNorma())
                .titulo(norma.getTitulo())
                .descricao(norma.getDescricao())
                .orgaoFiscalizador(norma.getOrgaoFiscalizador())
                .severidade(norma.getSeveridade())
                .build();
    }

    /**
     * Converte um DTO NormaAmbientalDTO para uma entidade NormaAmbiental
     * @param dto DTO NormaAmbientalDTO
     * @return Entidade NormaAmbiental
     */
    private NormaAmbiental converterParaEntidade(NormaAmbientalDTO dto) {
        return NormaAmbiental.builder()
                .id(dto.getId())
                .codigoNorma(dto.getCodigoNorma())
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .orgaoFiscalizador(dto.getOrgaoFiscalizador())
                .severidade(dto.getSeveridade())
                .build();
    }
}
