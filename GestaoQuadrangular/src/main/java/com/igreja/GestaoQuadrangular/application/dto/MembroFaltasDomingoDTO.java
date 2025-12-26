package com.igreja.GestaoQuadrangular.application.dto;

import com.igreja.GestaoQuadrangular.num.StatusEspiritual;

public record MembroFaltasDomingoDTO(
        Long id,
        String nome,
        String telefone,
        int faltasNoMes,
        StatusEspiritual status,
        String celulaNome
) {}
