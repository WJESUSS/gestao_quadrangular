package com.igreja.GestaoQuadrangular.application.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para exibir o dashboard completo da célula no frontend.
 * Contém informações básicas, frequência, visitantes e pendências.
 */
public record CelulaDashboardDTO(
        Long id,
        String nome,
        String diaSemana,
        String horario,
        String endereco,
        String nomeLider,
        int totalMembros,
        int presentesUltimaReuniao,
        double mediaFrequenciaUltimos3Meses,
        int visitantesUltimoMes,
        int relatoriosPendentes,
        LocalDate proximaReuniao
) {



    /**
     * Construtor auxiliar para criar um DTO com valores padrão seguros.
     * Útil quando alguns dados ainda não foram calculados (ex: no carregamento inicial).
     */
    public static CelulaDashboardDTO withDefaults(
            Long id,
            String nome,
            String diaSemana,
            String horario,
            String endereco,
            String nomeLider) {

        String liderSeguro = nomeLider != null && !nomeLider.isBlank()
                ? nomeLider
                : "Sem líder atribuído";

        return new CelulaDashboardDTO(
                id,
                nome,
                diaSemana,
                horario,
                endereco,
                liderSeguro,
                0,      // totalMembros
                0,      // presentesUltimaReuniao
                0.0,    // mediaFrequenciaUltimos3Meses
                0,      // visitantesUltimoMes
                0,      // relatoriosPendentes
                null    // proximaReuniao
        );
    }

    /**
     * Cria uma cópia do DTO com os dados de frequência atualizados.
     * Ideal para quando você calcula presença depois de carregar os dados básicos.
     */
    public CelulaDashboardDTO withAttendance(
            int totalMembros,
            int presentesUltimaReuniao,
            double mediaFrequenciaUltimos3Meses) {

        return new CelulaDashboardDTO(
                this.id,
                this.nome,
                this.diaSemana,
                this.horario,
                this.endereco,
                this.nomeLider,
                totalMembros,
                presentesUltimaReuniao,
                mediaFrequenciaUltimos3Meses,
                this.visitantesUltimoMes,
                this.relatoriosPendentes,
                this.proximaReuniao
        );
    }

    /**
     * Atualiza apenas os dados de visitantes e relatórios pendentes.
     */
    public CelulaDashboardDTO withVisitorsAndReports(
            int visitantesUltimoMes,
            int relatoriosPendentes) {

        return new CelulaDashboardDTO(
                this.id,
                this.nome,
                this.diaSemana,
                this.horario,
                this.endereco,
                this.nomeLider,
                this.totalMembros,
                this.presentesUltimaReuniao,
                this.mediaFrequenciaUltimos3Meses,
                visitantesUltimoMes,
                relatoriosPendentes,
                this.proximaReuniao
        );
    }

    /**
     * Atualiza a data da próxima reunião.
     */
    public CelulaDashboardDTO withProximaReuniao(LocalDate proximaReuniao) {
        return new CelulaDashboardDTO(
                this.id,
                this.nome,
                this.diaSemana,
                this.horario,
                this.endereco,
                this.nomeLider,
                this.totalMembros,
                this.presentesUltimaReuniao,
                this.mediaFrequenciaUltimos3Meses,
                this.visitantesUltimoMes,
                this.relatoriosPendentes,
                proximaReuniao
        );
    }
}