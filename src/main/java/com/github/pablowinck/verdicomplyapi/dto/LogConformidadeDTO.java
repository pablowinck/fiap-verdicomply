package com.github.pablowinck.verdicomplyapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogConformidadeDTO {

    private Long id;

    @NotNull(message = "A conformidade é obrigatória")
    private Long conformidadeId;

    @NotBlank(message = "A ação é obrigatória")
    @Size(max = 50, message = "A ação deve ter no máximo 50 caracteres")
    private String acao;

    @NotNull(message = "A data é obrigatória")
    private LocalDateTime dataHora;

    @NotBlank(message = "O usuário é obrigatório")
    @Size(max = 100, message = "O usuário deve ter no máximo 100 caracteres")
    private String usuario;

    @Size(max = 255, message = "As observações devem ter no máximo 255 caracteres")
    private String observacoes;

    private ConformidadeDTO conformidade;
}
