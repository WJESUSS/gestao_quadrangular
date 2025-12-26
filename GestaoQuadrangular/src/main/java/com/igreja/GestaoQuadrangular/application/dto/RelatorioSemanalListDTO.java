package com.igreja.GestaoQuadrangular.application.dto;

import java.time.LocalDate;

/**
 * DTO para listagem resumida de relatórios semanais (histórico no app)
 */
public class RelatorioSemanalListDTO {

    private Long id;
    private LocalDate dataRelatorio;
    private int presentes;
    private int visitantes;
    private int conversoes;
    private int batismos;

    public RelatorioSemanalListDTO() {}

    public RelatorioSemanalListDTO(Long id, LocalDate dataRelatorio, int presentes,
                                   int visitantes, int conversoes, int batismos) {
        this.id = id;
        this.dataRelatorio = dataRelatorio;
        this.presentes = presentes;
        this.visitantes = visitantes;
        this.conversoes = conversoes;
        this.batismos = batismos;
    }

    // Getters
    public Long getId() { return id; }
    public LocalDate getDataRelatorio() { return dataRelatorio; }
    public int getPresentes() { return presentes; }
    public int getVisitantes() { return visitantes; }
    public int getConversoes() { return conversoes; }
    public int getBatismos() { return batismos; }
}