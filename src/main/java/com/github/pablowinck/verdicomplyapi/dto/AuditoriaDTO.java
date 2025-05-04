package com.github.pablowinck.verdicomplyapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriaDTO {
    
    private Long id;
    
    @NotNull(message = "O departamento é obrigatório")
    private Long departamentoId;
    
    @PastOrPresent(message = "A data da auditoria não pode ser futura")
    private LocalDate dataAuditoria;
    
    @NotBlank(message = "O auditor responsável é obrigatório")
    private String auditorResponsavel;
    
    private String statusAuditoria;
    
    // Campo para apresentação de dados completos
    private DepartamentoDTO departamento;
}
