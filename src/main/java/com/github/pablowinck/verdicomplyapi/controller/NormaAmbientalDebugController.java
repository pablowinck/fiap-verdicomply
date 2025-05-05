package com.github.pablowinck.verdicomplyapi.controller;
import com.github.pablowinck.verdicomplyapi.dto.NormaAmbientalDTO;
import com.github.pablowinck.verdicomplyapi.model.NormaAmbiental;
import com.github.pablowinck.verdicomplyapi.repository.NormaAmbientalRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug/normas")
@Slf4j
public class NormaAmbientalDebugController {
    @Autowired
    private NormaAmbientalRepository normaAmbientalRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarNormasDebug() {
        try {
            List<NormaAmbiental> normas = normaAmbientalRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalNormas", normas.size());
            response.put("normasSimplificadas", normas.stream().map(norma -> {
                Map<String, Object> normaMap = new HashMap<>();
                normaMap.put("id", norma.getId());
                normaMap.put("codigoNorma", norma.getCodigoNorma());
                // Adicionar somente campos primitivos para evitar problemas de serialização
                return normaMap;
            }).toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("erro", "Falha ao buscar normas");
            error.put("causa", e.getMessage());
            error.put("classeCausa", e.getClass().getName());
            
            if (e.getCause() != null) {
                error.put("causaInterna", e.getCause().getMessage());
                error.put("classeCausaInterna", e.getCause().getClass().getName());
            }
            
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> contarNormas() {
        try {
            long total = normaAmbientalRepository.count();
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalNormas", total);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("erro", "Falha ao contar normas");
            error.put("causa", e.getMessage());
            error.put("classeCausa", e.getClass().getName());
            
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @GetMapping("/schema")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verificarSchema() {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            log.info("Verificando schema da tabela NORMA_AMBIENTAL");
            
            // Verifica a estrutura da tabela
            try {
                List<?> colunas = entityManager.createNativeQuery(
                    "SELECT COLUMN_NAME, DATA_TYPE FROM ALL_TAB_COLUMNS " +
                    "WHERE TABLE_NAME = 'NORMA_AMBIENTAL'").getResultList();
                resultado.put("colunas", colunas);
            } catch (Exception e) {
                log.error("Erro ao consultar schema: {}", e.getMessage());
                resultado.put("schemaErro", e.getMessage());
            }
            
            // Verifica restrições da tabela
            try {
                List<?> constraints = entityManager.createNativeQuery(
                    "SELECT CONSTRAINT_NAME, CONSTRAINT_TYPE, SEARCH_CONDITION " +
                    "FROM ALL_CONSTRAINTS WHERE TABLE_NAME = 'NORMA_AMBIENTAL'").getResultList();
                resultado.put("constraints", constraints);
            } catch (Exception e) {
                log.error("Erro ao consultar constraints: {}", e.getMessage());
                resultado.put("constraintsErro", e.getMessage());
            }
            
            // Verifica a estrutura do modelo
            resultado.put("modeloClasseNome", NormaAmbiental.class.getName());
            
            Field[] campos = NormaAmbiental.class.getDeclaredFields();
            List<Map<String, String>> camposInfo = new ArrayList<>();
            for (Field campo : campos) {
                Map<String, String> info = new HashMap<>();
                info.put("nome", campo.getName());
                info.put("tipo", campo.getType().getName());
                info.put("anotacoes", Arrays.toString(campo.getAnnotations()));
                camposInfo.add(info);
            }
            resultado.put("campos", camposInfo);
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro ao verificar schema: {}", e.getMessage(), e);
            resultado.put("erro", e.getMessage());
            resultado.put("stackTrace", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.status(500).body(resultado);
        }
    }
    
    @PostMapping("/create-sql")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> criarNormaNativa() {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            log.info("Criando norma ambiental via SQL nativo");
            
            try {
                int inserido = entityManager.createNativeQuery(
                    "INSERT INTO NORMA_AMBIENTAL (CODIGO_NORMA, DESCRICAO, ORGAO_FISCALIZADOR, TITULO, SEVERIDADE) " +
                    "VALUES ('SQL-001', 'Norma criada via SQL', 'TESTE', 'Titulo SQL', 'BAIXA')").executeUpdate();
                resultado.put("sucesso", inserido > 0);
                resultado.put("registrosInseridos", inserido);
            } catch (Exception e) {
                log.error("Erro ao inserir via SQL: {}", e.getMessage());
                resultado.put("erro", e.getMessage());
                resultado.put("stackTrace", Arrays.toString(e.getStackTrace()));
            }
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro geral: {}", e.getMessage(), e);
            resultado.put("erroGeral", e.getMessage());
            return ResponseEntity.status(500).body(resultado);
        }
    }
    
    @PostMapping("/create-jpa")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> criarNormaJPA(@RequestBody(required = false) NormaAmbientalDTO normaDTO) {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            log.info("Criando norma ambiental via JPA");
            
            NormaAmbiental norma = new NormaAmbiental();
            
            // Se não houver DTO, usa valores padrão
            if (normaDTO == null) {
                norma.setCodigoNorma("JPA-" + System.currentTimeMillis());
                norma.setDescricao("Norma criada via JPA");
                norma.setOrgaoFiscalizador("TESTE-JPA");
                norma.setTitulo("Titulo JPA");
                norma.setSeveridade("MEDIA");
            } else {
                norma.setCodigoNorma(normaDTO.getCodigoNorma());
                norma.setDescricao(normaDTO.getDescricao());
                norma.setOrgaoFiscalizador(normaDTO.getOrgaoFiscalizador());
                norma.setTitulo(normaDTO.getTitulo());
                norma.setSeveridade(normaDTO.getSeveridade());
            }
            
            try {
                norma = normaAmbientalRepository.save(norma);
                resultado.put("sucesso", true);
                resultado.put("norma", norma);
            } catch (Exception e) {
                log.error("Erro ao salvar via JPA: {}", e.getMessage());
                resultado.put("erro", e.getMessage());
                resultado.put("stackTrace", Arrays.toString(e.getStackTrace()));
            }
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro geral: {}", e.getMessage(), e);
            resultado.put("erroGeral", e.getMessage());
            return ResponseEntity.status(500).body(resultado);
        }
    }
    
    /**
     * Testa a conversão entre entidade e DTO
     * @return Resultado do teste
     */
    @GetMapping("/teste-conversao")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testeConversao() {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            log.info("Testando conversão entre entidade e DTO");
            
            // Cria uma entidade de teste
            NormaAmbiental norma = new NormaAmbiental();
            norma.setId(999L);
            norma.setCodigoNorma("TESTE-CONV");
            norma.setDescricao("Descrição de teste");
            norma.setOrgaoFiscalizador("TESTE");
            norma.setTitulo("Título de Teste");
            norma.setSeveridade("ALTA");
            
            // Converte manualmente para DTO
            NormaAmbientalDTO dto = new NormaAmbientalDTO();
            dto.setId(norma.getId());
            dto.setCodigoNorma(norma.getCodigoNorma());
            dto.setDescricao(norma.getDescricao());
            dto.setOrgaoFiscalizador(norma.getOrgaoFiscalizador());
            dto.setTitulo(norma.getTitulo());
            dto.setSeveridade(norma.getSeveridade());
            
            resultado.put("entidade", norma);
            resultado.put("dto", dto);
            resultado.put("sucesso", true);
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro no teste de conversão: {}", e.getMessage(), e);
            resultado.put("erro", e.getMessage());
            resultado.put("stackTrace", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.status(500).body(resultado);
        }
    }
}
