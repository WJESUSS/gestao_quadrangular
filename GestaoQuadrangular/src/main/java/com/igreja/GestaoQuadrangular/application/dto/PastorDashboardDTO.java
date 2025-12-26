package com.igreja.GestaoQuadrangular.application.dto;



public class PastorDashboardDTO {

    private long totalMembros;
    private long totalCelulas;
    private long membrosArquivados;
    private long totalLideres;

    public PastorDashboardDTO(long totalMembros, long totalCelulas, long membrosArquivados, long totalLideres) {
        this.totalMembros = totalMembros;
        this.totalCelulas = totalCelulas;
        this.membrosArquivados = membrosArquivados;
        this.totalLideres = totalLideres;
    }

    public long getTotalMembros() {
        return totalMembros;
    }

    public long getTotalCelulas() {
        return totalCelulas;
    }

    public long getMembrosArquivados() {
        return membrosArquivados;
    }

    public long getTotalLideres() {
        return totalLideres;
    }
}
