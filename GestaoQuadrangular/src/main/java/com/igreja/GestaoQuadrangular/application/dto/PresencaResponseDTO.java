package com.igreja.GestaoQuadrangular.application.dto;

public record PresencaResponseDTO(
        Long membroId,
        Long celulaId,
        String nomeMembro,
        String telefone, boolean presente
) {}
