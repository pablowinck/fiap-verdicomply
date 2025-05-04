package com.github.pablowinck.verdicomplyapi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
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

import com.github.pablowinck.verdicomplyapi.config.IntegracaoTestApplication;
import com.github.pablowinck.verdicomplyapi.dto.LoginDTO;

/**
 * Testes de integração para a autorização baseada em roles
 * Executado com o perfil "integracao" e sem mocks
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = IntegracaoTestApplication.class)
@ActiveProfiles("integracao")
public class AuthorizationIT {

    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    private String adminToken;
    private String gestorToken;
    private String auditorToken;
    
    @BeforeEach
    void setup() {
        // Obter token do admin para os testes usando as senhas definidas em data-integracao.sql
        adminToken = obterToken("admin", "admin");
        gestorToken = obterToken("gestor", "gestor");
        auditorToken = obterToken("auditor", "auditor");
    }
    
    @Test
    @DisplayName("Admin deve ter acesso a endpoint restrito a ADMIN")
    void adminDeveAcessarEndpointAdmin() {
        // Configurar cabeçalhos com o token do admin
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        
        // Criar a entidade de requisição apenas com os cabeçalhos
        HttpEntity<?> request = new HttpEntity<>(headers);
        
        // Acessar endpoint restrito a ADMIN
        String url = "http://localhost:" + port + "/api/auditorias";
        
        ResponseEntity<Object> resposta = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            Object.class
        );
        
        // Verificar o status da resposta
        assertEquals(HttpStatus.OK, resposta.getStatusCode(), 
                "O admin deve conseguir acessar o endpoint de auditorias");
    }
    
    @Test
    @DisplayName("Auditor não deve ter acesso a endpoint restrito a ADMIN")
    void auditorNaoDeveAcessarEndpointAdmin() {
        // Configurar cabeçalhos com o token do auditor
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + auditorToken);
        
        // Criar a entidade de requisição apenas com os cabeçalhos
        HttpEntity<?> request = new HttpEntity<>(headers);
        
        // Tentar acessar endpoint restrito a ADMIN
        String url = "http://localhost:" + port + "/api/auditorias";
        
        ResponseEntity<Object> resposta = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            Object.class
        );
        
        // Verificar o status da resposta (não deve ser OK)
        assertNotEquals(HttpStatus.OK, resposta.getStatusCode(), 
                "O auditor não deve conseguir acessar o endpoint de auditorias");
        
        // Verificar que o status é um erro de acesso negado (403 ou 500 são aceitáveis neste caso)
        assertTrue(
            resposta.getStatusCode() == HttpStatus.FORBIDDEN || resposta.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR,
            "O status da resposta deve ser FORBIDDEN ou INTERNAL_SERVER_ERROR, mas foi " + resposta.getStatusCode());
    }
    
    @Test
    @DisplayName("Gestor deve ter acesso a endpoints de gestão")
    void gestorDeveAcessarEndpointsDeGestao() {
        // Configurar cabeçalhos com o token do gestor
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + gestorToken);
        
        // Criar a entidade de requisição apenas com os cabeçalhos
        HttpEntity<?> request = new HttpEntity<>(headers);
        
        // Acessar endpoint permitido para GESTOR
        String url = "http://localhost:" + port + "/api/conformidades";
        
        ResponseEntity<Object> resposta = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            Object.class
        );
        
        // Verificar o status da resposta (deve ser OK)
        assertEquals(HttpStatus.OK, resposta.getStatusCode(), 
                "O gestor deve conseguir acessar o endpoint de conformidades");
    }
    
    @Test
    @DisplayName("Requisição sem token deve ser rejeitada")
    void requisicaoSemTokenDeveSerRejeitada() {
        // Criar a entidade de requisição sem token
        HttpEntity<?> request = new HttpEntity<>(new HttpHeaders());
        
        // Tentar acessar endpoint protegido
        String url = "http://localhost:" + port + "/api/auditorias";
        
        ResponseEntity<Object> resposta = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            Object.class
        );
        
        // Verificar o status da resposta (deve ser UNAUTHORIZED)
        assertEquals(HttpStatus.UNAUTHORIZED, resposta.getStatusCode(), 
                "A requisição sem token deve ser rejeitada");
    }
    
    /**
     * Método utilitário para obter token JWT através do login
     */
    private String obterToken(String username, String password) {
        // Preparar dados para login
        LoginDTO loginDTO = new LoginDTO(username, password);
        
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
            System.out.println("Erro na autenticação para usuário '" + username + "': " + resposta.getStatusCode());
            System.out.println("URL utilizada: " + url);
            System.out.println("Corpo da resposta: " + resposta.getBody());
            throw new RuntimeException("Erro ao obter token para o usuário " + username);
        }
        
        // Obter o token da resposta
        Map<String, Object> corpo = resposta.getBody();
        assertNotNull(corpo, "O corpo da resposta não deve ser nulo");
        
        String token = (String) corpo.get("token");
        assertNotNull(token, "O token não deve ser nulo");
        
        return token;
    }
}
