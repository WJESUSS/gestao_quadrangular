package com.igreja.GestaoQuadrangular.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
@Table(name = "transferencias_membresia")
public class TransferenciaMembresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membro_id", nullable = false)
    private Membro membro;

    @Column(nullable = false)
    private LocalDate dataTransferencia;

    @Column(nullable = false)
    private String tipo; // "ENTRADA" ou "SAIDA"

    @Column(length = 150)
    private String igrejaOrigemDestino;

    @Column(length = 1000)
    private String motivo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Membro getMembro() {
        return membro;
    }

    public void setMembro(Membro membro) {
        this.membro = membro;
    }

    public LocalDate getDataTransferencia() {
        return dataTransferencia;
    }

    public void setDataTransferencia(LocalDate dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getIgrejaOrigemDestino() {
        return igrejaOrigemDestino;
    }

    public void setIgrejaOrigemDestino(String igrejaOrigemDestino) {
        this.igrejaOrigemDestino = igrejaOrigemDestino;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
// Getters e Setters
}