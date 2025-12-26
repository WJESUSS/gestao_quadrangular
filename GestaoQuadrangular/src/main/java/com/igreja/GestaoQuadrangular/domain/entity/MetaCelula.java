package com.igreja.GestaoQuadrangular.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class MetaCelula {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "celula_id", nullable = false)
    private Celula celula;

    private String descricao;            // ex: "Alcançar 20 membros até dez/2025"
    private int alvoMembros;             // meta de membros
    private int alvoVisitasSemanais;     // meta média de visitantes por semana
    private LocalDate dataInicio;
    private LocalDate dataFim;

    // getters e setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Celula getCelula() {
        return celula;
    }

    public void setCelula(Celula celula) {
        this.celula = celula;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getAlvoMembros() {
        return alvoMembros;
    }

    public void setAlvoMembros(int alvoMembros) {
        this.alvoMembros = alvoMembros;
    }

    public int getAlvoVisitasSemanais() {
        return alvoVisitasSemanais;
    }

    public void setAlvoVisitasSemanais(int alvoVisitasSemanais) {
        this.alvoVisitasSemanais = alvoVisitasSemanais;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
    @Column(nullable = false)
    private boolean ativa = true;  // meta ativa por padrão

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }
}