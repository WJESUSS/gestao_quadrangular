package com.igreja.GestaoQuadrangular.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record RegistrarPresencaSimplesDTO(

        @NotNull(message = "A data da reunião é obrigatória")
        LocalDate data,

        @NotEmpty(message = "Deve haver pelo menos um membro presente")
        List<Long> presentesIds

) {}