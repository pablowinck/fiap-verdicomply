package com.github.pablowinck.verdicomplyapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConformidadeDTO {

    private Long id;

    @NotNull(message = "A auditoria é obrigatória")
    private Long auditoriaId;

    @NotNull(message = "A norma ambiental é obrigatória")
    private Long normaAmbientalId;

    @NotBlank(message = "O status de conformidade é obrigatório")
    @Pattern(regexp = "^[SN]$", message = "O valor deve ser 'S' para conforme ou 'N' para não conforme")
    private String estaConforme;

    @Size(max = 200, message = "A observação deve ter no máximo 200 caracteres")
    private String observacao;

    private AuditoriaDTO auditoria;
    private NormaAmbientalDTO normaAmbiental;
}
