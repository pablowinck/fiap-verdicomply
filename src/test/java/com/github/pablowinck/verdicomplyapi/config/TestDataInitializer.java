package com.github.pablowinck.verdicomplyapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.github.pablowinck.verdicomplyapi.model.Usuario;
import com.github.pablowinck.verdicomplyapi.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Inicializador de dados para testes de integracao
 * Esta classe garante que os dados de teste estarão disponíveis para os testes de integração
 * Obs: A criação das tabelas é responsabilidade do Flyway
 */
@Configuration
@Profile("integracao")
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TestDataInitializer {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Inicializa os dados de teste quando o contexto da aplicação é carregado
     */
    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void onApplicationEvent() {
        log.info("Verificando necessidade de dados adicionais para testes");
        try {
            // Verificar se já existem dados no banco (via data.sql)
            if (usuarioRepository.findByUsername("admin").isPresent()) {
                log.info("Usuários já existem no banco de dados (via data.sql). Nada a fazer.");
            } else {
                log.info("Dados não encontrados. Inserindo dados de teste manualmente.");
                initUsers();
            }
        } catch (Exception e) {
            log.error("Erro ao verificar/inicializar dados: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Inicializa usuários para testes apenas se necessário
     */
    @Transactional
    public void initUsers() {
        try {
            log.info("Criando usuários de teste adicionais");
            
            // Admin (caso não exista)
            if (!usuarioRepository.findByUsername("admin").isPresent()) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole("ADMIN");
                usuarioRepository.save(admin);
                log.info("Usuário 'admin' criado com sucesso");
            }
            
            // Gestor (caso não exista)
            if (!usuarioRepository.findByUsername("gestor").isPresent()) {
                Usuario gestor = new Usuario();
                gestor.setUsername("gestor");
                gestor.setPassword(passwordEncoder.encode("gestor"));
                gestor.setRole("GESTOR");
                usuarioRepository.save(gestor);
                log.info("Usuário 'gestor' criado com sucesso");
            }
            
            // Auditor (caso não exista)
            if (!usuarioRepository.findByUsername("auditor").isPresent()) {
                Usuario auditor = new Usuario();
                auditor.setUsername("auditor");
                auditor.setPassword(passwordEncoder.encode("auditor"));
                auditor.setRole("AUDITOR");
                usuarioRepository.save(auditor);
                log.info("Usuário 'auditor' criado com sucesso");
            }
            
            log.info("Verificação/criação de usuários finalizada");
        } catch (Exception e) {
            log.error("Erro ao inicializar usuários: {}", e.getMessage(), e);
        }
    }
}
