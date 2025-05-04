package com.github.pablowinck.verdicomplyapi.security;

import com.github.pablowinck.verdicomplyapi.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implementação de UserDetailsService que carrega usuários do banco de dados
 */
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUserDetailsService.class);
    
    private final UsuarioRepository usuarioRepository;

    public DatabaseUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Tentando carregar usuário: {}", username);
        return usuarioRepository.findByUsername(username)
                .map(usuario -> {
                    logger.debug("Usuário {} encontrado. Role: {}", username, usuario.getRole());
                    logger.debug("Senha armazenada: {}", usuario.getPassword());
                    
                    return User.builder()
                        .username(usuario.getUsername())
                        .password(usuario.getPassword())
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRole())))
                        .build();
                })
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado: " + username);
                });
    }
}
