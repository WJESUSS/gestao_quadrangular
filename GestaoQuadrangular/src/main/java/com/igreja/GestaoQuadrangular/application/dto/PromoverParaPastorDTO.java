package com.igreja.GestaoQuadrangular.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public record PromoverParaPastorDTO(
        @NotNull(message = "ID do usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "Data de ordenação é obrigatória")
        String dataOrdenacao,  // formato: "2025-12-27"

        @NotNull(message = "Igreja de ordenação é obrigatória")
        String igrejaOrdenacao,

        boolean pastorPrincipal
) {}