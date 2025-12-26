package com.igreja.GestaoQuadrangular.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Entity
public class Encontro {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String tipo; // "Encontro", "Pré-Encontro", "Reencontro"

    @Column
    private LocalDate data;

    @ManyToMany
    private List<Membro> participantes;

    /**
     * Retorna uma visão imutável da lista de participantes.
     * Evita modificações externas na coleção.
     */
    public List<Membro> getParticipantes() {
        if (participantes == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(participantes);
    }

    // Getters/Setters
}