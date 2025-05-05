package com.github.pablowinck.verdicomplyapi.integration;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

import com.github.pablowinck.verdicomplyapi.config.IntegrationTestConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Testes de integração para o fluxo de registro de usuários
 * Executado com o perfil "integracao" e sem mocks
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integracao")
@Import(IntegrationTestConfig.class)
public class RegistroControllerIT {

    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void deveRegistrarNovoUsuario() {
        // Preparar dados para registro como um Map
        Map<String, Object> registroData = new HashMap<>();
        registroData.put("username", "novo_usuario");
        registroData.put("password", "senha123");
        registroData.put("role", "AUDITOR");
        
        // Configurar cabeçalhos da requisição
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Criar a entidade de requisição
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(registroData, headers);
        
        // Executar a requisição para o endpoint de registro
        String url = "http://localhost:" + port + "/api/public/registro";
        
        @SuppressWarnings("rawtypes")
        ResponseEntity<HashMap> resposta = restTemplate.postForEntity(url, request, HashMap.class);
        
        // Verificar o status da resposta
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode(), 
                "O status da resposta deve ser 201 CREATED");
        
        // Verificar o conteúdo da resposta
        @SuppressWarnings("unchecked")
        Map<String, Object> corpo = resposta.getBody();
        assertNotNull(corpo, "O corpo da resposta não deve ser nulo");
        
        String username = (String) corpo.get("username");
        assertEquals("novo_usuario", username, "O username deve corresponder ao informado no registro");
        
        String role = (String) corpo.get("role");
        assertEquals("AUDITOR", role, "O role deve corresponder ao informado no registro");
    }
}
