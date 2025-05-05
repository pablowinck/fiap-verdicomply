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
 * Inicializador de dados para ambiente de produção
 * Esta classe garante que dados essenciais (como usuário admin) estão disponíveis
 */
@Configuration
@Profile("prod")
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProdDataInitializer {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private DepartamentoRepository departamentoRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Inicializa os dados essenciais quando o contexto da aplicação é carregado
     */
    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void onApplicationEvent() {
        log.info("Inicializando dados essenciais para ambiente de produção");
        try {
            initUsers();
            initDepartamentos();
            log.info("Dados essenciais inicializados com sucesso");
        } catch (Exception e) {
            log.error("Erro ao inicializar dados essenciais: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Inicializa usuários essenciais
     */
    @Transactional
    public void initUsers() {
        try {
            // Admin
            System.out.println("Verificando usuário admin...");
            Usuario adminUser = usuarioRepository.findByUsername("admin").orElse(null);
            
            if (adminUser == null) {
                System.out.println("Criando usuário admin...");
                adminUser = new Usuario();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin"));
                adminUser.setRole("ADMIN");
                usuarioRepository.save(adminUser);
                System.out.println("Usuário admin criado com sucesso!");
            } else {
                System.out.println("Usuário admin já existe!");
            }
            
            // Gestor
            Usuario gestorUser = usuarioRepository.findByUsername("gestor").orElse(null);
            
            if (gestorUser == null) {
                gestorUser = new Usuario();
                gestorUser.setUsername("gestor");
                gestorUser.setPassword(passwordEncoder.encode("gestor"));
                gestorUser.setRole("GESTOR");
                usuarioRepository.save(gestorUser);
            }
            
            // Auditor
            Usuario auditorUser = usuarioRepository.findByUsername("auditor").orElse(null);
            
            if (auditorUser == null) {
                auditorUser = new Usuario();
                auditorUser.setUsername("auditor");
                auditorUser.setPassword(passwordEncoder.encode("auditor"));
                auditorUser.setRole("AUDITOR");
                usuarioRepository.save(auditorUser);
            }
            
        } catch (Exception e) {
            log.error("Erro ao inicializar usuários: {}", e.getMessage(), e);
            throw e; // Propagar erro para detectar falhas
        }
    }
    
    /**
     * Inicializa departamentos essenciais
     */
    @Transactional
    public void initDepartamentos() {
        try {
            // Departamentos essenciais
            createDepartamentoIfNotExists("Manufatura");
            createDepartamentoIfNotExists("Logística");
            createDepartamentoIfNotExists("Operações");
            
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
