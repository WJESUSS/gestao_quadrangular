package com.igreja.GestaoQuadrangular.application.dto;

import com.igreja.GestaoQuadrangular.num.TipoReuniao;

import java.time.LocalDate;
import java.util.List;

public record RegistrarPresencaCultoDTO(
        TipoReuniao tipoReuniao,          // ex: CULTO_DOMINGO_NOITE
        LocalDate data,
        List<Long> presentesIds
) {}
