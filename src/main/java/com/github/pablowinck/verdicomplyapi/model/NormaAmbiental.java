package com.github.pablowinck.verdicomplyapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "norma_ambiental")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormaAmbiental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_norma")
    private Long id;

    @NotNull
    @Column(name = "codigo_norma", nullable = false, length = 20)
    private String codigoNorma;

    @Column(name = "titulo", length = 100)
    private String titulo;

    @Column(name = "descricao", length = 200)
    private String descricao;

    @Column(name = "orgao_fiscalizador", length = 100)
    private String orgaoFiscalizador;

    @Column(name = "severidade", length = 20)
    private String severidade;
}
