package com.igreja.GestaoQuadrangular.application.dto;

import com.igreja.GestaoQuadrangular.num.TipoOferta;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContribuicaoCreateDTO(
        Long membroId,
        TipoOferta tipoOferta,
        BigDecimal valor,
        LocalDate data,
        String observacao,
        String numeroRelatorioSecretaria
) {}
