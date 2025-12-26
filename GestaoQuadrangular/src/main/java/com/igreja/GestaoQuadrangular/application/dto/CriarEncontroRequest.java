package com.igreja.GestaoQuadrangular.application.dto;

import java.time.LocalDate;

public record CriarEncontroRequest(
        LocalDate data,
        String tipo // ex: "REUNIÃO DE LÍDERES", "TREINAMENTO", "CÉLULA"
) {}
