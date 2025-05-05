package com.github.pablowinck.verdicomplyapi.integration;


import com.github.pablowinck.verdicomplyapi.config.IntegrationTestConfig;
import com.github.pablowinck.verdicomplyapi.dto.LoginDTO;
import com.github.pablowinck.verdicomplyapi.dto.RegistroDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Testes de integração para o fluxo completo de autenticação
 * Executado com o perfil "integracao" e sem mocks
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integracao")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(IntegrationTestConfig.class)
public class FluxoAutenticacaoIT {

    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    // Gerar username único para evitar conflitos entre execuções
    private static final String USERNAME = "usuario_teste_" + UUID.randomUUID().toString().substring(0, 8);
    private static final String PASSWORD = "senha123";
    private static final String ROLE = "ADMIN";
    
    private static String token;
    
    @Test
    @Order(1)
    @DisplayName("1 - Deve registrar um novo usuário com sucesso")
    void deveRegistrarNovoUsuario() {
        // Preparar dados para registro
        RegistroDTO registroDTO = new RegistroDTO(USERNAME, PASSWORD, ROLE);
        
        // Configurar cabeçalhos da requisição
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Criar a entidade de requisição
        HttpEntity<RegistroDTO> request = new HttpEntity<>(registroDTO, headers);
        
        // Executar a requisição para o endpoint de registro
        String url = "http://localhost:" + port + "/api/public/registro";
        
        // Usar tipo parametrizado com cast para evitar warnings
        @SuppressWarnings({"rawtypes", "unchecked"})
        ResponseEntity<HashMap> resposta = restTemplate.postForEntity(url, request, HashMap.class);
        
        // Extrair o corpo da resposta
        Map<String, Object> corpo = resposta.getBody();
        
        // Verificar o status da resposta
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode(), 
                "O status da resposta deve ser 201 CREATED");
        
        // Verificar o conteúdo da resposta
        assertNotNull(corpo, "O corpo da resposta não deve ser nulo");
        
        String username = (String) corpo.get("username");
        assertEquals(USERNAME, username, "O username deve corresponder ao informado no registro");
        
        String role = (String) corpo.get("role");
        assertEquals(ROLE, role, "O role deve corresponder ao informado no registro");
        
        System.out.println("Usuário registrado com sucesso: " + USERNAME);
    }
    
    @Test
    @Order(2)
    @DisplayName("2 - Deve realizar login com as credenciais do usuário registrado")
    void deveRealizarLoginComCredenciaisValidas() {
        // Preparar dados para login
        LoginDTO loginDTO = new LoginDTO(USERNAME, PASSWORD);
        
        // Configurar cabeçalhos da requisição
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Criar a entidade de requisição
        HttpEntity<LoginDTO> request = new HttpEntity<>(loginDTO, headers);
        
        // Executar a requisição para o endpoint de login
        String url = "http://localhost:" + port + "/api/public/auth/login";
        
        @SuppressWarnings("rawtypes")
        ResponseEntity<HashMap> resposta = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            HashMap.class
        );
        
        // Verificar o status da resposta
        assertEquals(HttpStatus.OK, resposta.getStatusCode(), 
                "O status da resposta deve ser 200 OK");
        
        // Verificar se há um token na resposta
        @SuppressWarnings("unchecked")
        Map<String, Object> corpo = resposta.getBody();
        assertNotNull(corpo, "O corpo da resposta não deve ser nulo");
        
        token = (String) corpo.get("token");
        assertNotNull(token, "O token não deve ser nulo");
        
        System.out.println("Login realizado com sucesso, token obtido");
    }
    
    @Test
    @Order(3)
    @DisplayName("3 - Deve acessar endpoint protegido com o token obtido")
    void deveAcessarEndpointProtegido() {
        // Verificar se o token foi obtido no teste anterior
        assertNotNull(token, "O token deve ter sido obtido no teste de login");
        
        // Configurar cabeçalhos com o token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        
        // Criar a entidade de requisição apenas com os cabeçalhos
        HttpEntity<?> request = new HttpEntity<>(headers);
        
        // Acessar endpoint protegido para ADMIN
        String url = "http://localhost:" + port + "/api/auditorias";
        
        @SuppressWarnings("rawtypes")
        ResponseEntity<List> resposta = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            List.class
        );
        
        // Verificar o status da resposta
        assertEquals(HttpStatus.OK, resposta.getStatusCode(), 
                "O status da resposta deve ser 200 OK, indicando acesso autorizado");
        
        System.out.println("Acesso autorizado ao endpoint protegido");
    }
}
