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

import com.github.pablowinck.verdicomplyapi.model.Departamento;
import com.github.pablowinck.verdicomplyapi.model.Usuario;
import com.github.pablowinck.verdicomplyapi.repository.DepartamentoRepository;
import com.github.pablowinck.verdicomplyapi.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Inicializador de dados para testes de integracao
 * Esta classe garante que os dados de teste estarão disponíveis para os testes de integração
 * A criação das tabelas é responsabilidade do Hibernate (ddl-auto=create-drop)
 */
@Configuration
@Profile("integracao")
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TestDataInitializer {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private DepartamentoRepository departamentoRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Inicializa os dados de teste quando o contexto da aplicação é carregado
     */
    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void onApplicationEvent() {
        log.info("Inicializando dados de teste para ambiente de integração");
        try {
            // Sempre inicializar os dados de teste para evitar problemas de dependência
            initUsers();
            initDepartamentos();
            log.info("Dados de teste inicializados com sucesso");
        } catch (Exception e) {
            log.error("Erro ao inicializar dados de teste: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Inicializa usuários para testes
     */
    @Transactional
    public void initUsers() {
        try {
            log.info("Criando usuários de teste");
            
            // Admin 
            createUserIfNotExists("admin", "admin", "ADMIN");
            
            // Gestor 
            createUserIfNotExists("gestor", "gestor", "GESTOR");
            
            // Auditor 
            createUserIfNotExists("auditor", "auditor", "AUDITOR");
            
            log.info("Usuários de teste criados com sucesso");
        } catch (Exception e) {
            log.error("Erro ao inicializar usuários: {}", e.getMessage(), e);
            throw e; // Propagar erro para detectar falhas
        }
    }
    
    /**
     * Método auxiliar para criar usuário se não existir
     */
    private Usuario createUserIfNotExists(String username, String password, String role) {
        return usuarioRepository.findByUsername(username)
                .orElseGet(() -> {
                    Usuario user = new Usuario();
                    user.setUsername(username);
                    user.setPassword(passwordEncoder.encode(password));
                    user.setRole(role);
                    return usuarioRepository.save(user);
                });
    }
    
    /**
     * Inicializa departamentos para testes
     */
    @Transactional
    public void initDepartamentos() {
        try {
            log.info("Criando departamentos de teste");
            
            // Departamentos para testes
            createDepartamentoIfNotExists("Manufatura_Integracao");
            createDepartamentoIfNotExists("Logística_Integracao");
            createDepartamentoIfNotExists("Operações_Integracao");
            
            log.info("Departamentos de teste criados com sucesso");
        } catch (Exception e) {
            log.error("Erro ao inicializar departamentos: {}", e.getMessage(), e);
            throw e; // Propagar erro para detectar falhas
        }
    }
    
    /**
     * Método auxiliar para criar departamento se não existir
     */
    private Departamento createDepartamentoIfNotExists(String nome) {
        return departamentoRepository.findByNomeDepartamento(nome)
                .orElseGet(() -> {
                    Departamento departamento = new Departamento();
                    departamento.setNomeDepartamento(nome);
                    return departamentoRepository.save(departamento);
                });
    }
}
