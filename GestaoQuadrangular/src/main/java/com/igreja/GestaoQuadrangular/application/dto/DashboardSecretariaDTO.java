package com.igreja.GestaoQuadrangular.application.dto;

import com.igreja.GestaoQuadrangular.application.dto.MembroDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Casamento;
import com.igreja.GestaoQuadrangular.domain.entity.Visitante;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record DashboardSecretariaDTO(

        // Aniversariantes
        List<MembroDTO> aniversariantesSemana,
        List<MembroDTO> aniversariantesMes,

        // Inativos
        long totalMembrosInativos,
        List<MembroDTO> inativosMaisDeMeses, // ex: > 3 meses sem presença

        // Visitantes
        long totalVisitantesUltimos30Dias,
        long visitantesQueRetornaram,
        double taxaRetornoPercentual,

        // Próximos eventos
        List<Casamento> proximosCasamentos, // próximos 60 dias

        // Dados incompletos
        long membrosSemFoto,
        long membrosSemEmailOuTelefone,

        // Alertas pendentes
        List<Visitante> visitantesPendentesConversao, // > 30 dias
        long totalPendentesConversao,                 // ← adição útil

        // Gráfico exemplo
        Map<String, Double> frequenciaMediaPorMes,    // ex: {"Jan/2025": 85.5}

        // Data de referência (opcional, ajuda no frontend)
        LocalDate dataReferencia
) {}