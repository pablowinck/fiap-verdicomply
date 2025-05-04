package com.github.pablowinck.verdicomplyapi.service;

import com.github.pablowinck.verdicomplyapi.dto.RegistroDTO;
import com.github.pablowinck.verdicomplyapi.model.Usuario;
import com.github.pablowinck.verdicomplyapi.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Busca um usuário pelo nome de usuário
     * @param username Nome de usuário
     * @return Usuário encontrado (Optional)
     */
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Lista todos os usuários
     * @return Lista de usuários
     */
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /**
     * Registra um novo usuário no sistema
     * @param registroDTO DTO com dados para registro
     * @return Usuário registrado
     * @throws IllegalArgumentException se o nome de usuário já existir
     */
    @Transactional
    public Usuario registrarUsuario(RegistroDTO registroDTO) {
        // Verificar se username já existe
        if (usuarioRepository.findByUsername(registroDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Nome de usuário já existe");
        }

        // Criar e salvar o novo usuário
        Usuario usuario = new Usuario();
        usuario.setUsername(registroDTO.getUsername());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setRole(registroDTO.getRole());

        return usuarioRepository.save(usuario);
    }
}
