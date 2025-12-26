package com.igreja.GestaoQuadrangular.application.dto;

import java.time.LocalDate;

public record PastorResponseDTO(
        Long id,
        Long usuarioId,
        String nome,
        String email,
        String telefone,
        LocalDate dataOrdenacao,
        String igrejaOrdenacao,
        boolean pastorPrincipal
) {}
