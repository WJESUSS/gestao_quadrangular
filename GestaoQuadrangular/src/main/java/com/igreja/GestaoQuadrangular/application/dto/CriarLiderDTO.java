package com.igreja.GestaoQuadrangular.application.dto;

public record CriarLiderDTO(
        Long usuarioId,
        String nome,
        String email,
        String telefone
) {}