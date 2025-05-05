package com.github.pablowinck.verdicomplyapi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;


import com.github.pablowinck.verdicomplyapi.model.Usuario;
import com.github.pablowinck.verdicomplyapi.repository.UsuarioRepository;

/**
 * Testes de integração para verificar o carregamento correto dos usuários
 * no banco de dados durante os testes
 */
@SpringBootTest
@ActiveProfiles("integracao")
public class UsuarioRepositoryIT {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    @DisplayName("Deve carregar usuário admin corretamente")
    void deveCarregarUsuarioAdmin() {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername("admin");
        
        assertTrue(usuarioOpt.isPresent(), "Usuário admin deve existir no banco");
        
        Usuario admin = usuarioOpt.get();
        assertEquals("admin", admin.getUsername(), "Nome de usuário deve ser 'admin'");
        assertEquals("ADMIN", admin.getRole(), "Role deve ser 'ADMIN'");
        
        // Verifica se a senha armazenada é um hash BCrypt
        String senhaCodificada = admin.getPassword();
        assertNotNull(senhaCodificada, "A senha não deve ser nula");
        System.out.println("Senha codificada do admin: " + senhaCodificada);
        
        // Verifica se a senha 'admin' é validada contra o hash armazenado
        boolean senhaCorreta = passwordEncoder.matches("admin", senhaCodificada);
        assertTrue(senhaCorreta, "A senha 'admin' deve corresponder ao hash armazenado");
    }
}
