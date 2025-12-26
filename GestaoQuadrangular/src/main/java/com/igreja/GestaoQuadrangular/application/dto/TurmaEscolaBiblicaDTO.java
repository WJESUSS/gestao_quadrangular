package com.igreja.GestaoQuadrangular.application.dto;

import com.igreja.GestaoQuadrangular.domain.entity.TurmaEscolaBiblica;

import java.time.LocalDate;
import java.util.List;

public record TurmaEscolaBiblicaDTO(
        Long id,
        String nome,
        TurmaEscolaBiblica.TipoTurma tipoTurma,
        LocalDate dataInicio,
        LocalDate dataFim,
        String professor,
        int capacidadeMaxima,
        int totalInscritos,
        boolean lotada,
        List<MembroResumoDTO> inscritos  // opcional: lista resumida dos inscritos
) {
    // Record já gera construtor, getters, etc.
}

// DTO resumido para evitar expor todos os dados do membro
