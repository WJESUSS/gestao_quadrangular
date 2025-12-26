package com.igreja.GestaoQuadrangular.application.dto;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO para criação de Relatório Semanal (usado pelo líder no app com poucos cliques)
 */
public class RelatorioSemanalCreateDTO {

    private Long celulaId;
    private LocalDate dataRelatorio;       // data da reunião (ex: data da célula daquela semana)
    private int presentes;                 // quantidade de membros presentes
    private int visitantes;                // quantidade de visitantes
    private int conversoes;                // decisões / conversões
    private int batismos;                  // batismos decididos
    private Set<Long> membrosPresentesIds; // opcional: IDs dos membros que marcaram presença

    // Construtores
    public RelatorioSemanalCreateDTO() {}

    public RelatorioSemanalCreateDTO(Long celulaId, LocalDate dataRelatorio, int presentes,
                                     int visitantes, int conversoes, int batismos,
                                     Set<Long> membrosPresentesIds) {
        this.celulaId = celulaId;
        this.dataRelatorio = dataRelatorio;
        this.presentes = presentes;
        this.visitantes = visitantes;
        this.conversoes = conversoes;
        this.batismos = batismos;
        this.membrosPresentesIds = membrosPresentesIds;
    }

    // Getters e Setters
    public Long getCelulaId() { return celulaId; }
    public void setCelulaId(Long celulaId) { this.celulaId = celulaId; }

    public LocalDate getDataRelatorio() { return dataRelatorio; }
    public void setDataRelatorio(LocalDate dataRelatorio) { this.dataRelatorio = dataRelatorio; }

    public int getPresentes() { return presentes; }
    public void setPresentes(int presentes) { this.presentes = presentes; }

    public int getVisitantes() { return visitantes; }
    public void setVisitantes(int visitantes) { this.visitantes = visitantes; }

    public int getConversoes() { return conversoes; }
    public void setConversoes(int conversoes) { this.conversoes = conversoes; }

    public int getBatismos() { return batismos; }
    public void setBatismos(int batismos) { this.batismos = batismos; }

    public Set<Long> getMembrosPresentesIds() { return membrosPresentesIds; }
    public void setMembrosPresentesIds(Set<Long> membrosPresentesIds) { this.membrosPresentesIds = membrosPresentesIds; }
}