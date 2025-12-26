package com.igreja.GestaoQuadrangular.application.dto;

// src/main/java/com/igreja/GestaoQuadrangular/application/dto/CelulaResponseDTO.java


public record CelulaResponseDTO(
        Long id,
        String nome,
        String endereco,
        String diaSemana,
        String horario,
        boolean casaDePaz,
        Long liderId,
        String liderNome
) {}