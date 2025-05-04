package com.github.pablowinck.verdicomplyapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "CONFORMIDADE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conformidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONFORMIDADE")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AUDITORIA", nullable = false)
    private Auditoria auditoria;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_NORMA", nullable = false)
    private NormaAmbiental norma;

    @Pattern(regexp = "[SN]", message = "O valor deve ser 'S' para conforme ou 'N' para não conforme")
    @Column(name = "ESTA_CONFORME", length = 1)
    private String estaConforme;

    @Column(name = "OBSERVACAO", length = 200)
    private String observacao;
}
