package com.github.pablowinck.verdicomplyapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogConformidadeDTO {

    private Long id;

    @NotNull(message = "A conformidade é obrigatória")
    private Long conformidadeId;

    @NotBlank(message = "A ação é obrigatória")
    @Size(max = 20, message = "A ação deve ter no máximo 20 caracteres")
    private String acao;

    @NotNull(message = "A data de registro é obrigatória")
    private LocalDate dataRegistro;

    @Size(max = 200, message = "Os detalhes devem ter no máximo 200 caracteres")
    private String detalhes;

    private ConformidadeDTO conformidade;
}
