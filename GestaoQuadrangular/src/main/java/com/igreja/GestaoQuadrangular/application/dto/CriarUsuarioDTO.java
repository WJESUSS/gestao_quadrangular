package com.igreja.GestaoQuadrangular.application.dto;

public record CriarUsuarioDTO(
        String nome,
        String email,
        String senha,
        String telefone
) {}
