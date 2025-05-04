package com.github.pablowinck.verdicomplyapi.config;

import com.github.pablowinck.verdicomplyapi.security.JwtProperties;
import com.github.pablowinck.verdicomplyapi.security.JwtTokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@Profile("test")
public class TestSecurityConfig {

    /**
     * Bean para personalizar a segurança da aplicação web durante os testes
     * Está configurado para ignorar todas as requisições de segurança
     */
    @Bean
    @Primary
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/**");
    }

    /**
     * Configuração de segurança para testes que desabilita completamente todos os filtros de segurança
     * e permite todas as requisições sem autenticação
     */
    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .anonymous(AbstractHttpConfigurer::disable)
            // Desabilita todos os filtros de segurança para os testes
            .securityContext(AbstractHttpConfigurer::disable)
            .sessionManagement(AbstractHttpConfigurer::disable);
        
        return http.build();
    }
    
    /**
     * Bean para fornecer um encoder de senha simples para os testes
     */
    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }
            
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }
    
    /**
     * Bean para fornecer usuários em memória para os testes
     */
    @Bean
    @Primary
    public InMemoryUserDetailsManager userDetailsManager() {
        UserDetails auditor = User.builder()
                .username("auditor")
                .password("auditor123")
                .roles("AUDITOR")
                .build();
        
        UserDetails gestor = User.builder()
                .username("gestor")
                .password("gestor123")
                .roles("GESTOR", "AUDITOR")
                .build();
        
        UserDetails admin = User.builder()
                .username("admin")
                .password("admin123")
                .roles("ADMIN", "GESTOR", "AUDITOR")
                .build();
        
        return new InMemoryUserDetailsManager(auditor, gestor, admin);
    }
    
    /**
     * Mock do JwtTokenProvider para testes
     */
    @Bean
    @Primary
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(new JwtProperties()) {
            @Override
            public String createToken(org.springframework.security.core.Authentication authentication) {
                return "test-token";
            }
            
            @Override
            public boolean validateToken(String token) {
                return true;
            }
            
            @Override
            public org.springframework.security.core.Authentication getAuthentication(String token) {
                return null;
            }
        };
    }
    
    /**
     * Mock das propriedades JWT para testes
     */
    @Bean
    @Primary
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }
    
    /**
     * Mock do AuthenticationManager para testes
     */
    @Bean
    @Primary
    public AuthenticationManager authenticationManager() {
        return authentication -> authentication;
    }
}
