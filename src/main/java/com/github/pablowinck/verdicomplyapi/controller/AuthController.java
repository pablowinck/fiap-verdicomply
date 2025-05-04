package com.github.pablowinck.verdicomplyapi.controller;

import com.github.pablowinck.verdicomplyapi.dto.LoginDTO;
import com.github.pablowinck.verdicomplyapi.dto.TokenDTO;
import com.github.pablowinck.verdicomplyapi.security.JwtProperties;
import com.github.pablowinck.verdicomplyapi.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final JwtProperties jwtProperties;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, JwtProperties jwtProperties) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());
        
        try {
            Authentication authentication = authenticationManager.authenticate(authToken);
            
            String token = tokenProvider.createToken(authentication);
            
            String[] roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toArray(String[]::new);
            
            TokenDTO tokenDTO = TokenDTO.builder()
                    .tipo(jwtProperties.getPrefix().trim())
                    .token(token)
                    .username(authentication.getName())
                    .roles(roles)
                    .build();
            
            return ResponseEntity.ok(tokenDTO);
        } catch (BadCredentialsException e) {
            // Usar um objeto TokenDTO simplificado para o erro
            TokenDTO errorResponse = new TokenDTO();
            errorResponse.setTipo("erro");
            errorResponse.setToken(null);
            errorResponse.setUsername(loginDTO.getUsername());
            errorResponse.setRoles(new String[0]);
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }
    }
}
