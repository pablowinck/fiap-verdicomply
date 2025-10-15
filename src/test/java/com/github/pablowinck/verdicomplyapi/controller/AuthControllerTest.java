package com.github.pablowinck.verdicomplyapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pablowinck.verdicomplyapi.config.TestSecurityConfig;
import com.github.pablowinck.verdicomplyapi.controller.exception.ManipuladorGlobalDeExcecoes;
import com.github.pablowinck.verdicomplyapi.dto.LoginDTO;
import com.github.pablowinck.verdicomplyapi.security.JwtProperties;
import com.github.pablowinck.verdicomplyapi.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({TestSecurityConfig.class, ManipuladorGlobalDeExcecoes.class})
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private AuthenticationManager authenticationManager;
    
    @MockitoBean
    private JwtTokenProvider tokenProvider;
    
    @MockitoBean
    private JwtProperties jwtProperties;

    @Test
    @DisplayName("Deve autenticar usuário e retornar token JWT")
    void deveAutenticarUsuarioERetornarToken() throws Exception {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("admin", "admin123");
        String token = "jwt-token-exemplo";
        
        // Cria um usuário do Spring Security com as autoridades necessárias
        User userDetails = new User(
                "admin",
                "admin123",
                Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_GESTOR"),
                        new SimpleGrantedAuthority("ROLE_AUDITOR")
                )
        );
        
        // Cria a autenticação usando um objeto real em vez de um mock
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        
        when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.createToken(authentication)).thenReturn(token);
        when(jwtProperties.getPrefix()).thenReturn("Bearer ");

        // Act & Assert
        mockMvc.perform(post("/api/public/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.roles.length()").value(3));
    }

    @Test
    @DisplayName("Deve retornar 401 para credenciais inválidas")
    void deveRetornar401ParaCredenciaisInvalidas() throws Exception {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("usuario-invalido", "senha-invalida");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Credenciais inválidas"));

        // Act & Assert
        // Com o GlobalExceptionHandler, o erro agora retorna 401
        mockMvc.perform(post("/api/public/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized());
    }
}
