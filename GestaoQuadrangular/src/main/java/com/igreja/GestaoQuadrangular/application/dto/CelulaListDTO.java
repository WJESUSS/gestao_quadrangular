package com.igreja.GestaoQuadrangular.application.dto;

// CelulaListDTO.java
public record CelulaListDTO(
        Long id,
        String nome,
        String diaSemana,
        String horario,
        String endereco,
        String nomeLider,
        String emailLider,
        int quantidadeMembros,
        java.time.LocalDateTime dataCriacao
) {}