package com.github.pablowinck.verdicomplyapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "pendencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pendencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pendencia")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conformidade", nullable = false)
    private Conformidade conformidade;

    @Column(name = "descricao_pendencia", length = 200)
    private String descricaoPendencia;

    @Column(name = "prazo_resolucao")
    private LocalDate prazoResolucao;

    @Pattern(regexp = "[SN]", message = "O valor deve ser 'S' para resolvida ou 'N' para não resolvida")
    @Column(name = "resolvida", length = 1)
    private String resolvida;
}
