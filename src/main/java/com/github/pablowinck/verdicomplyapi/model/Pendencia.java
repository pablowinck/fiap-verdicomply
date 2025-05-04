package com.github.pablowinck.verdicomplyapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "PENDENCIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pendencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PENDENCIA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONFORMIDADE", nullable = false)
    private Conformidade conformidade;

    @Column(name = "DESCRICAO_PENDENCIA", length = 200)
    private String descricaoPendencia;

    @Column(name = "PRAZO_RESOLUCAO")
    private LocalDate prazoResolucao;

    @Pattern(regexp = "[SN]", message = "O valor deve ser 'S' para resolvida ou 'N' para não resolvida")
    @Column(name = "RESOLVIDA", length = 1)
    private String resolvida;
}
