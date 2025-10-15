package com.github.pablowinck.verdicomplyapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class PendenciaDTO {

    private Long id;

    @NotNull(message = "A conformidade é obrigatória")
    private Long conformidadeId;

    @NotBlank(message = "A descrição da pendência é obrigatória")
    @Size(max = 200, message = "A descrição deve ter no máximo 200 caracteres")
    private String descricaoPendencia;

    @NotNull(message = "O prazo de resolução é obrigatório")
    private LocalDate prazoResolucao;

    @NotBlank(message = "O status de resolução é obrigatório")
    @Pattern(regexp = "^[SN]$", message = "O valor deve ser 'S' para resolvida ou 'N' para não resolvida")
    private String resolvida;

    private ConformidadeDTO conformidade;
}
