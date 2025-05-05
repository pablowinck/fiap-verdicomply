package com.github.pablowinck.verdicomplyapi.controller;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para corrigir problemas com a tabela NORMA_AMBIENTAL
 */
@RestController
@RequestMapping("/api/fix/normas")
@Slf4j
public class FixNormaAmbientalController {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Verifica o schema da tabela NORMA_AMBIENTAL
     */
    @GetMapping("/schema")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> verificarSchema() {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            log.info("Verificando schema da tabela NORMA_AMBIENTAL");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> colunas = entityManager.createNativeQuery(
                "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'NORMA_AMBIENTAL'", 
                Map.class).getResultList();
            
            resultado.put("colunas", colunas);
            resultado.put("sucesso", true);
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro ao verificar schema: {}", e.getMessage(), e);
            resultado.put("erro", e.getMessage());
            resultado.put("classeErro", e.getClass().getName());
            return ResponseEntity.status(500).body(resultado);
        }
    }
    
    /**
     * Corrige o schema da tabela NORMA_AMBIENTAL
     */
    @PostMapping("/fix-schema")
    @PreAuthorize("permitAll()")
    @Transactional
    public ResponseEntity<Map<String, Object>> corrigirSchema() {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            log.info("Corrigindo schema da tabela NORMA_AMBIENTAL");
            
            try {
                entityManager.createNativeQuery(
                    "ALTER TABLE NORMA_AMBIENTAL ADD COLUMN IF NOT EXISTS TITULO VARCHAR(100)")
                    .executeUpdate();
                resultado.put("colunaTituloAdicionada", true);
            } catch (Exception e) {
                log.error("Erro ao adicionar coluna TITULO: {}", e.getMessage());
                resultado.put("erroTitulo", e.getMessage());
            }
            
            try {
                entityManager.createNativeQuery(
                    "ALTER TABLE NORMA_AMBIENTAL ADD COLUMN IF NOT EXISTS SEVERIDADE VARCHAR(50)")
                    .executeUpdate();
                resultado.put("colunaSeveridadeAdicionada", true);
            } catch (Exception e) {
                log.error("Erro ao adicionar coluna SEVERIDADE: {}", e.getMessage());
                resultado.put("erroSeveridade", e.getMessage());
            }
            
            try {
                int atualizados = entityManager.createNativeQuery(
                    "UPDATE NORMA_AMBIENTAL SET TITULO = CODIGO_NORMA WHERE TITULO IS NULL")
                    .executeUpdate();
                resultado.put("registrosAtualizadosTitulo", atualizados);
            } catch (Exception e) {
                log.error("Erro ao atualizar TITULO: {}", e.getMessage());
                resultado.put("erroAtualizarTitulo", e.getMessage());
            }
            
            try {
                int atualizados = entityManager.createNativeQuery(
                    "UPDATE NORMA_AMBIENTAL SET SEVERIDADE = 'MEDIA' WHERE SEVERIDADE IS NULL")
                    .executeUpdate();
                resultado.put("registrosAtualizadosSeveridade", atualizados);
            } catch (Exception e) {
                log.error("Erro ao atualizar SEVERIDADE: {}", e.getMessage());
                resultado.put("erroAtualizarSeveridade", e.getMessage());
            }
            
            resultado.put("sucesso", true);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro geral ao corrigir schema: {}", e.getMessage(), e);
            resultado.put("erroGeral", e.getMessage());
            resultado.put("classeErroGeral", e.getClass().getName());
            return ResponseEntity.status(500).body(resultado);
        }
    }
    
    /**
     * Recria as normas ambientais básicas
     */
    @PostMapping("/recriar-normas")
    @PreAuthorize("permitAll()")
    @Transactional
    public ResponseEntity<Map<String, Object>> recriarNormas() {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            log.info("Recriando normas ambientais básicas");
            
            // Limpar tabela existente
            try {
                int removidos = entityManager.createNativeQuery("DELETE FROM NORMA_AMBIENTAL").executeUpdate();
                resultado.put("registrosRemovidos", removidos);
            } catch (Exception e) {
                log.error("Erro ao limpar tabela: {}", e.getMessage());
                resultado.put("erroLimpar", e.getMessage());
            }
            
            // Criar normas com schema completo
            String[][] normas = {
                {"CONAMA-001", "Resolução CONAMA nº 001", "CONAMA", "Resolução CONAMA nº 001/1986", "ALTA"},
                {"ISO-14001", "Norma ISO 14001 - Sistema de Gestão Ambiental", "ISO", "ISO 14001 - Gestão Ambiental", "ALTA"},
                {"NBR-10004", "Classificação de Resíduos Sólidos", "ABNT", "NBR 10004 - Resíduos Sólidos", "MEDIA"},
                {"LEI-12305", "Política Nacional de Resíduos Sólidos", "FEDERAL", "Lei 12.305 - PNRS", "ALTA"},
                {"LEI-9605", "Lei de Crimes Ambientais", "FEDERAL", "Lei 9.605 - Crimes Ambientais", "ALTA"}
            };
            
            int criados = 0;
            for (String[] norma : normas) {
                try {
                    entityManager.createNativeQuery(
                        "INSERT INTO NORMA_AMBIENTAL (CODIGO_NORMA, DESCRICAO, ORGAO_FISCALIZADOR, TITULO, SEVERIDADE) VALUES (?, ?, ?, ?, ?)")
                        .setParameter(1, norma[0])
                        .setParameter(2, norma[1])
                        .setParameter(3, norma[2])
                        .setParameter(4, norma[3])
                        .setParameter(5, norma[4])
                        .executeUpdate();
                    criados++;
                } catch (Exception e) {
                    log.error("Erro ao criar norma {}: {}", norma[0], e.getMessage());
                    resultado.put("erro_" + norma[0], e.getMessage());
                }
            }
            
            resultado.put("normasCriadas", criados);
            resultado.put("sucesso", criados > 0);
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro geral ao recriar normas: {}", e.getMessage(), e);
            resultado.put("erroGeral", e.getMessage());
            return ResponseEntity.status(500).body(resultado);
        }
    }
}
