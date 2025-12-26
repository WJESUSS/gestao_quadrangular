package com.igreja.GestaoQuadrangular.domain.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "cultos")
public class Culto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private LocalDate data;

    @Setter
    @Column(nullable = false)
    private String tipo;
    // Ex: DOMINGO_MANHA, DOMINGO_NOITE, QUARTA, CELULA

    @Setter
    private String descricao;

    // ========= GETTERS E SETTERS =========

    public Long getId() {
        return id;
    }

    public LocalDate getData() {
        return data;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

}

