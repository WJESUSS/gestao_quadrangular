package com.igreja.GestaoQuadrangular.application.dto;

import java.time.LocalDate;

public record AtualizarEncontroRequest(
        LocalDate data,
        String tipo
) {}
