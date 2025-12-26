package com.igreja.GestaoQuadrangular.application.dto;

public record LiderResponseDTO(
        Long id,
        String nome,
        String telefone,
        String email,
        int totalCelulas  // número de células que ele lidera
) {}