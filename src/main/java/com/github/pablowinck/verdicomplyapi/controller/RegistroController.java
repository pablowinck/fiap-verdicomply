package com.github.pablowinck.verdicomplyapi.controller;

import com.github.pablowinck.verdicomplyapi.dto.RegistroDTO;
import com.github.pablowinck.verdicomplyapi.model.Usuario;
import com.github.pablowinck.verdicomplyapi.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para gerenciar o registro de usuários
 */
@RestController
@RequestMapping("/api/public/registro")
public class RegistroController {

    private final UsuarioService usuarioService;

    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint para registrar um novo usuário
     * @param registroDTO DTO com dados para registro
     * @return Resposta com informações do usuário registrado
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarUsuario(@Valid @RequestBody RegistroDTO registroDTO) {
        try {
            Usuario usuario = usuarioService.registrarUsuario(registroDTO);
            
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("username", usuario.getUsername());
            resposta.put("role", usuario.getRole());
            resposta.put("mensagem", "Usuário registrado com sucesso");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
