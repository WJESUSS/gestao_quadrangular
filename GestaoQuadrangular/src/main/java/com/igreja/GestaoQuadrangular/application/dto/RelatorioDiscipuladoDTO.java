package com.igreja.GestaoQuadrangular.application.dto;

import com.igreja.GestaoQuadrangular.num.EscadaSucesso;

import java.time.LocalDate;

public record RelatorioDiscipuladoDTO(
        Long membroId,
        String membroNome,
        EscadaSucesso etapaAtual,
        LocalDate dataUltimaAtualizacao,
        String observacao
) {}