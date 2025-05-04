package com.github.pablowinck.verdicomplyapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "AUDITORIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AUDITORIA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DEPARTAMENTO", nullable = false)
    private Departamento departamento;

    @Column(name = "DATA_AUDITORIA")
    private LocalDate dataAuditoria;

    @Column(name = "AUDITOR_RESPONSAVEL", length = 100)
    private String auditorResponsavel;

    @Column(name = "STATUS_AUDITORIA", length = 20)
    private String statusAuditoria;
}
