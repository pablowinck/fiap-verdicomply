package com.github.pablowinck.verdicomplyapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "LOG_CONFORMIDADE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogConformidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LOG")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONFORMIDADE")
    private Conformidade conformidade;

    @Column(name = "ACAO", length = 20)
    private String acao;

    @Column(name = "DATA_REGISTRO")
    private LocalDate dataRegistro;

    @Column(name = "DETALHES", length = 200)
    private String detalhes;
}
