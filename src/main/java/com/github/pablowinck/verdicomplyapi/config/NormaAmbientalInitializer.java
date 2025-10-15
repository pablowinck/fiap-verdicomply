package com.github.pablowinck.verdicomplyapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.Transactional;

import com.github.pablowinck.verdicomplyapi.model.NormaAmbiental;
import com.github.pablowinck.verdicomplyapi.repository.NormaAmbientalRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Inicializador de normas ambientais para ambientes de teste e produção
 * Esta classe garante que dados essenciais (como normas ambientais básicas) estão disponíveis
 */
// Desabilitado temporariamente para evitar problemas durante migração para PostgreSQL
//@Configuration
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class NormaAmbientalInitializer {

    @Autowired
    private NormaAmbientalRepository normaAmbientalRepository;

    /**
     * Inicializa as normas ambientais essenciais quando o contexto da aplicação é carregado
     */
    //@EventListener(ContextRefreshedEvent.class)
    //@Transactional
    public void onApplicationEvent() {
        log.info("Inicializando normas ambientais essenciais");
        try {
            initNormasAmbientais();
            log.info("Normas ambientais inicializadas com sucesso");
        } catch (Exception e) {
            log.error("Erro ao inicializar normas ambientais: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Inicializa normas ambientais essenciais
     */
    @Transactional
    public void initNormasAmbientais() {
        try {
            // Criar normas ambientais básicas se não existirem
            createNormaAmbientalIfNotExists(
                "CONAMA-001", 
                "Resolução CONAMA nº 001", 
                "Critérios básicos e diretrizes para avaliação de impacto ambiental", 
                "CONAMA", 
                "Média"
            );
            
            createNormaAmbientalIfNotExists(
                "ISO-14001", 
                "ISO 14001:2015", 
                "Sistema de Gestão Ambiental", 
                "ISO", 
                "Alta"
            );
            
            createNormaAmbientalIfNotExists(
                "NBR-10004", 
                "NBR 10004", 
                "Classificação de resíduos sólidos", 
                "ABNT", 
                "Alta"
            );
            
            createNormaAmbientalIfNotExists(
                "LEI-12305", 
                "Lei 12.305/2010", 
                "Política Nacional de Resíduos Sólidos", 
                "Governo Federal", 
                "Alta"
            );
            
            createNormaAmbientalIfNotExists(
                "LEI-9605", 
                "Lei 9.605/1998", 
                "Lei de Crimes Ambientais", 
                "Governo Federal", 
                "Alta"
            );
            
        } catch (Exception e) {
            log.error("Erro ao inicializar normas ambientais: {}", e.getMessage(), e);
            throw e; // Propagar erro para detectar falhas
        }
    }
    
    /**
     * Método auxiliar para criar norma ambiental se não existir
     */
    private NormaAmbiental createNormaAmbientalIfNotExists(
            String codigoNorma, 
            String titulo, 
            String descricao, 
            String orgaoFiscalizador, 
            String severidade) {
        
        return normaAmbientalRepository.findByCodigoNorma(codigoNorma)
                .orElseGet(() -> {
                    log.info("Criando norma ambiental: {}", codigoNorma);
                    NormaAmbiental norma = new NormaAmbiental();
                    norma.setCodigoNorma(codigoNorma);
                    norma.setTitulo(titulo);
                    norma.setDescricao(descricao);
                    norma.setOrgaoFiscalizador(orgaoFiscalizador);
                    norma.setSeveridade(severidade);
                    return normaAmbientalRepository.save(norma);
                });
    }
}
