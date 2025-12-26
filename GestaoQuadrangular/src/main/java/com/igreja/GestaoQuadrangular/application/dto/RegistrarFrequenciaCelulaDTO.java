package com.igreja.GestaoQuadrangular.application.dto;

import java.time.LocalDate;
import java.util.List;

public record RegistrarFrequenciaCelulaDTO(
        LocalDate data,
        List<Long> presentesIds
) {}