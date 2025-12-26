package com.igreja.GestaoQuadrangular.application.dto;

import java.util.Map;

public record DashboardGrowthDTO(
        int totalMembros,
        int totalCelulas,
        Map<String, Integer> membrosPorStatus,  // "VERDE": 50, "AMARELO": 20, "VERMELHO": 10
        Map<String, Integer> crescimentoMensal  // "2025-01": 100, "2025-02": 120, ...
) {}