package com.github.pablowinck.verdicomplyapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartamentoDTO {
    
    private Long id;
    
    @NotBlank(message = "O nome do departamento é obrigatório")
    private String nomeDepartamento;
}
