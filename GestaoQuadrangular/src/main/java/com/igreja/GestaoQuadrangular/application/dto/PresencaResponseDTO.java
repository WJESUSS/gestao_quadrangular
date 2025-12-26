package com.igreja.GestaoQuadrangular.application.dto;

public record PresencaResponseDTO(
        Long membroId,
        String nomeMembro,
        String telefone, boolean presente
) {}
