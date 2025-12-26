package com.igreja.GestaoQuadrangular.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardFinanceiroDTO(
        // Total por período
        LocalDate periodoInicio,
        LocalDate periodoFim,
        BigDecimal totalArrecadado,

        // Gráfico de tipos de oferta
        List<TipoOfertaResumoDTO> tiposOferta,

        // Membros fiéis e infiéis (baseado no ano atual)
        List<MembroFielDTO> membrosFieis,
        List<MembroFielDTO> membrosInfieis,

        // Arrecadação por célula
        List<ArrecadacaoCelulaDTO> arrecadacaoPorCelula,

        // Comparativo mensal (últimos 12 meses)
        List<ComparativoMensalDTO> comparativoMensal
) {}

