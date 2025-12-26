package com.igreja.GestaoQuadrangular.application.dto;

/**
 * DTO com progresso da meta atual (usado para exibir gráfico no app)
 */
public class MetaProgressoDTO {

    private String descricao;
    private int membrosAtuais;
    private int alvoMembros;
    private double progressoMembrosPorcento;     // 0 a 100
    private double mediaVisitantesUltimasSemanas;
    private int alvoVisitasSemanais;
    private double progressoVisitasPorcento;     // 0 a 100

    public MetaProgressoDTO() {}

    public MetaProgressoDTO(String descricao, int membrosAtuais, int alvoMembros,
                            double progressoMembrosPorcento, double mediaVisitantesUltimasSemanas,
                            int alvoVisitasSemanais, double progressoVisitasPorcento) {
        this.descricao = descricao;
        this.membrosAtuais = membrosAtuais;
        this.alvoMembros = alvoMembros;
        this.progressoMembrosPorcento = progressoMembrosPorcento;
        this.mediaVisitantesUltimasSemanas = mediaVisitantesUltimasSemanas;
        this.alvoVisitasSemanais = alvoVisitasSemanais;
        this.progressoVisitasPorcento = progressoVisitasPorcento;
    }

    // Getters
    public String getDescricao() { return descricao; }
    public int getMembrosAtuais() { return membrosAtuais; }
    public int getAlvoMembros() { return alvoMembros; }
    public double getProgressoMembrosPorcento() { return progressoMembrosPorcento; }
    public double getMediaVisitantesUltimasSemanas() { return mediaVisitantesUltimasSemanas; }
    public int getAlvoVisitasSemanais() { return alvoVisitasSemanais; }
    public double getProgressoVisitasPorcento() { return progressoVisitasPorcento; }
}