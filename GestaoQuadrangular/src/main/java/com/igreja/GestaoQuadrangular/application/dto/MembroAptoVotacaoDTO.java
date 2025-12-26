package com.igreja.GestaoQuadrangular.application.dto;

public record MembroAptoVotacaoDTO(
        Long id,
        String nome,
        String statusEspiritual, // ou o que for necessário
        boolean podeVotar      // ou critério que você usa
        // NÃO inclua turmasInscritas aqui!
) {}