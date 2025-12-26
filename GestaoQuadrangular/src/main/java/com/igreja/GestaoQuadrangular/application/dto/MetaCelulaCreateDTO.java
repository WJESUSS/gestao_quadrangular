package com.igreja.GestaoQuadrangular.application.dto;

import java.time.LocalDate;

/**
 * DTO para criação/atualização de meta da célula
 */
public class MetaCelulaCreateDTO {

    private Long celulaId;
    private String descricao;              // ex: "Chegar a 25 membros até dezembro/2025"
    private int alvoMembros;               // meta de quantidade de membros
    private int alvoVisitasSemanais;       // média desejada de visitantes por semana
    private LocalDate dataInicio;
    private LocalDate dataFim;

    public MetaCelulaCreateDTO() {}

    // Getters e Setters
    public Long getCelulaId() { return celulaId; }
    public void setCelulaId(Long celulaId) { this.celulaId = celulaId; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getAlvoMembros() { return alvoMembros; }
    public void setAlvoMembros(int alvoMembros) { this.alvoMembros = alvoMembros; }

    public int getAlvoVisitasSemanais() { return alvoVisitasSemanais; }
    public void setAlvoVisitasSemanais(int alvoVisitasSemanais) { this.alvoVisitasSemanais = alvoVisitasSemanais; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
}