package com.igreja.GestaoQuadrangular.application.dto;

public record CelulaListResponseDTO(
        Long id,
        String nome,
        String endereco,
        String diaSemana,
        String horario,
        boolean casaDePaz,
        Long liderId,
        String liderNome,
        int quantidadeMembros  // opcional: quantos membros tem na célula
) {}
