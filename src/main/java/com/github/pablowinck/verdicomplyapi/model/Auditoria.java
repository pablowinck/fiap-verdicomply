package com.github.pablowinck.verdicomplyapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento", nullable = false)
    private Departamento departamento;

    @Column(name = "data_auditoria")
    private LocalDate dataAuditoria;

    @Column(name = "auditor_responsavel", length = 100)
    private String auditorResponsavel;

    @Column(name = "status_auditoria", length = 20)
    private String statusAuditoria;
}
