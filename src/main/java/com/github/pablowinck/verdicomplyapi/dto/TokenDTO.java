package com.github.pablowinck.verdicomplyapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {
    
    private String tipo;
    private String token;
    private String username;
    private String[] roles;
}
