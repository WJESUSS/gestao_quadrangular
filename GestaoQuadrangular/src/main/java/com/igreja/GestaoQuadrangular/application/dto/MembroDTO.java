package com.igreja.GestaoQuadrangular.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.igreja.GestaoQuadrangular.num.EscadaSucesso;
import com.igreja.GestaoQuadrangular.num.StatusEspiritual;

import java.time.LocalDate;

public record MembroDTO(

        Long id,

        String nome,

        String email,

        String telefone,

        // Nome da célula (se houver)
        String nomeCelula,

        // Nome do líder da célula (se houver)
        String nomeLiderCelula,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataUltimaAtualizacaoEscada,

        EscadaSucesso escadaSucesso,  // ← adicionado

        StatusEspiritual statusEspiritual,  // ← adicionado

        Integer faltasConsecutivas,

        boolean arquivado,

        String observacaoDiscipulado

) {
        // Não precisa de construtor manual — o record gera automaticamente
        // com todos esses 12 parâmetros na ordem exata acima
}