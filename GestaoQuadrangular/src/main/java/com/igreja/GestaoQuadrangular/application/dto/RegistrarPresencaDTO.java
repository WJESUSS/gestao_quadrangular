package com.igreja.GestaoQuadrangular.application.dto;

import java.time.LocalDate;
import java.util.List;

public record RegistrarPresencaDTO(
        LocalDate data,                    // ex: 2025-12-22
        List<Long> presentesIds           // IDs dos membros presentes
) {}
