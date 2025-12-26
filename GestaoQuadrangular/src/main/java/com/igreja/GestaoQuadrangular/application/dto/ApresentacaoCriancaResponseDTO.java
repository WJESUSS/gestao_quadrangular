package com.igreja.GestaoQuadrangular.application.dto;

import java.time.LocalDate;

public record ApresentacaoCriancaResponseDTO(
        Long id,
        String nomeCrianca,
        LocalDate dataNascimentoCrianca,
        LocalDate dataApresentacao,

        // Exibição unificada (lógica no service ou mapper)
        String nomePai,
        String nomeMae,

        String nomePastorOficiante,

        // Úteis para frontend/relatórios
        Boolean paisSaoMembros,
        String telefoneContato,
        String observacoes
) {}