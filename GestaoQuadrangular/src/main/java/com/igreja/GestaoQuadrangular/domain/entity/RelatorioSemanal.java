package com.igreja.GestaoQuadrangular.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@JsonIgnoreProperties({"membros", "lider"})
@Table(name = "relatorios_semanais")
public class RelatorioSemanal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "celula_id", nullable = false)
    private Celula celula;

    @Column(nullable = false)
    private LocalDate dataRelatorio;

    @Column(nullable = false)
    private int presentes;

    @Column(nullable = false)
    private int visitantes;

    @Column(nullable = false)
    private int conversoes;

    @Column(nullable = false)
    private int batismos;

    @ElementCollection
    @CollectionTable(name = "relatorio_membros_presentes", joinColumns = @JoinColumn(name = "relatorio_id"))
    @Column(name = "membro_id")
    private Set<Long> membrosPresentesIds = new HashSet<>();

    // ==================== Construtores ====================

    public RelatorioSemanal() {}

    // ==================== Getters e Setters ====================

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

    public LocalDate getDataRelatorio() {
        return dataRelatorio;
    }

    public void setDataRelatorio(LocalDate dataRelatorio) {
        this.dataRelatorio = dataRelatorio;
    }

    public int getPresentes() {
        return presentes;
    }

    public void setPresentes(int presentes) {
        this.presentes = presentes;
    }

    public int getVisitantes() {
        return visitantes;
    }

    public void setVisitantes(int visitantes) {
        this.visitantes = visitantes;
    }

    public int getConversoes() {
        return conversoes;
    }

    public void setConversoes(int conversoes) {
        this.conversoes = conversoes;
    }

    public int getBatismos() {
        return batismos;
    }

    public void setBatismos(int batismos) {
        this.batismos = batismos;
    }

    public Set<Long> getMembrosPresentesIds() {
        return membrosPresentesIds;
    }

    public void setMembrosPresentesIds(Set<Long> membrosPresentesIds) {
        this.membrosPresentesIds = membrosPresentesIds;
    }

}