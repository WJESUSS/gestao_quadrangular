package com.igreja.GestaoQuadrangular.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RelatorioFinanceiroDTO(
        LocalDate periodoInicio,
        LocalDate periodoFim,
        BigDecimal totalDizimos,
        BigDecimal totalOfertas,
        BigDecimal totalGeral,
        List<EntradaPorCelulaDTO> entradasPorCelula
) {}