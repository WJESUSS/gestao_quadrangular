package com.igreja.GestaoQuadrangular.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@Table(name = "casamentos")
public class Casamento {

    // Getters e Setters
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "noivo_id", nullable = false)
    private Membro noivo;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "noiva_id", nullable = false)
    private Membro noiva;

    @Getter
    @Setter
    @Column(nullable = false)
    private LocalDate dataCasamento;

    @Column(length = 150)
    private String local;

    @ManyToOne
    @JoinColumn(name = "pastor_oficiante_id")
    private Pastor pastorOficiante;

    @Column(length = 1000)
    private String observacoes;

    // ... outros getters/setters
}