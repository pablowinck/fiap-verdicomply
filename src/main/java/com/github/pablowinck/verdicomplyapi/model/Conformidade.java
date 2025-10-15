package com.github.pablowinck.verdicomplyapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "conformidade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conformidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conformidade")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_auditoria", nullable = false)
    private Auditoria auditoria;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_norma", nullable = false)
    private NormaAmbiental norma;

    @Pattern(regexp = "[SN]", message = "O valor deve ser 'S' para conforme ou 'N' para não conforme")
    @Column(name = "esta_conforme", length = 1)
    private String estaConforme;

    @Column(name = "observacao", length = 200)
    private String observacao;
}
