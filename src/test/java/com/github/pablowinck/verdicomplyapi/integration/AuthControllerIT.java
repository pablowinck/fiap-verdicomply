package com.github.pablowinck.verdicomplyapi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

import com.github.pablowinck.verdicomplyapi.config.IntegrationTestConfig;
import com.github.pablowinck.verdicomplyapi.dto.LoginDTO;

/**
 * Testes de integração para o fluxo de autenticação
 * Executado com o perfil "integracao" e sem mocks
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integracao")
@Import(IntegrationTestConfig.class)
public class AuthControllerIT {

    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("Deve realizar login com credenciais válidas")
    void deveRealizarLoginComCredenciaisValidas() {
        // Preparar dados para login
        // No data-integracao.sql, a senha é 'admin' para o usuário 'admin'
        LoginDTO loginDTO = new LoginDTO("admin", "admin");
        
        // Configurar cabeçalhos da requisição
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Criar a entidade de requisição
        HttpEntity<LoginDTO> request = new HttpEntity<>(loginDTO, headers);
        
        // Executar a requisição para o endpoint de login
        String url = "http://localhost:" + port + "/api/public/auth/login";
        
        ResponseEntity<Map<String, Object>> resposta = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        // Verificar o status da resposta
        if (resposta.getStatusCode() != HttpStatus.OK) {
            System.out.println("Erro na autenticação: " + resposta.getStatusCode());
            System.out.println("Corpo da resposta: " + resposta.getBody());
        }
        assertEquals(HttpStatus.OK, resposta.getStatusCode(), 
                "O status da resposta deve ser 200 OK");
        
        // Verificar se há um token na resposta
        Map<String, Object> corpo = resposta.getBody();
        assertNotNull(corpo, "O corpo da resposta não deve ser nulo");
        
        String token = (String) corpo.get("token");
        assertNotNull(token, "O token não deve ser nulo");
    }
    
    @Test
    @DisplayName("Deve retornar erro com credenciais inválidas")
    void deveRetornarErroComCredenciaisInvalidas() {
        // Preparar dados para login com credenciais inválidas
        // Preparar dados para login com senha incorreta
        // A senha correta seria "admin" conforme definido no data-integracao.sql
        LoginDTO loginDTO = new LoginDTO("admin", "senha_incorreta");
        
        // Configurar cabeçalhos da requisição
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Criar a entidade de requisição
        HttpEntity<LoginDTO> request = new HttpEntity<>(loginDTO, headers);
        
        // Executar a requisição para o endpoint de login
        String url = "http://localhost:" + port + "/api/public/auth/login";
        
        ResponseEntity<Map<String, Object>> resposta = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        // Verificar se a resposta é um erro (401 - Unauthorized)
        assertEquals(HttpStatus.UNAUTHORIZED, resposta.getStatusCode(), 
                "O status da resposta deve ser 401 Unauthorized");
    }
}
