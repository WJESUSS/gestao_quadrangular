package com.igreja.GestaoQuadrangular.application.dto;

public record CadastrarPerfilPastorDTO(
        Long usuarioId,
        String dataOrdenacao,        // formato YYYY-MM-DD
        String igrejaOrdenacao,
        boolean pastorPrincipal
) {}
