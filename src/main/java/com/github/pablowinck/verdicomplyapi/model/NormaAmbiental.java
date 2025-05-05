package com.github.pablowinck.verdicomplyapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "NORMA_AMBIENTAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormaAmbiental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_NORMA")
    private Long id;

    @NotNull
    @Column(name = "CODIGO_NORMA", nullable = false, length = 20)
    private String codigoNorma;
    
    @Column(name = "TITULO", length = 100)
    private String titulo;

    @Column(name = "DESCRICAO", length = 200)
    private String descricao;

    @Column(name = "ORGAO_FISCALIZADOR", length = 100)
    private String orgaoFiscalizador;
    
    @Column(name = "SEVERIDADE", length = 20)
    private String severidade;
}
