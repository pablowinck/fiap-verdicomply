package com.github.pablowinck.verdicomplyapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NormaAmbientalDTO {

    private Long id;

    @NotBlank(message = "O código da norma é obrigatório")
    @Size(max = 20, message = "O código deve ter no máximo 20 caracteres")
    private String codigo;

    @NotBlank(message = "A descrição da norma é obrigatória")
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    private String descricao;

    @NotBlank(message = "O órgão fiscalizador é obrigatório")
    @Size(max = 100, message = "O órgão fiscalizador deve ter no máximo 100 caracteres")
    private String orgaoFiscalizador;

    @Size(max = 255, message = "As observações devem ter no máximo 255 caracteres")
    private String observacoes;
}
