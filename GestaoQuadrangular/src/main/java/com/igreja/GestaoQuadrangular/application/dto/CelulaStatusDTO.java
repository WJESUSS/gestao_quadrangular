package com.igreja.GestaoQuadrangular.application.dto;



public record CelulaStatusDTO(
        Long id,
        String nome,
        String endereco,
        String diaSemana,
        String horario,
        boolean casaDePaz,
        String nomeLider,
        String status,          // "Aberta (Casa de Paz)" ou "Pendente"
        int totalMembros
) {}
