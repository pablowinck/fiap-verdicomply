package com.github.pablowinck.verdicomplyapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "log_conformidade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogConformidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conformidade")
    private Conformidade conformidade;

    @Column(name = "acao", length = 20)
    private String acao;

    @Column(name = "data_registro")
    private LocalDate dataRegistro;

    @Column(name = "detalhes", length = 200)
    private String detalhes;
}
