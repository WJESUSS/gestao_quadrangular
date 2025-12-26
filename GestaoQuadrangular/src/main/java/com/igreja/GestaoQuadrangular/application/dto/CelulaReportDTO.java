package com.igreja.GestaoQuadrangular.application.dto;

import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class CelulaReportDTO {

    private final String celulaNome;
    private final String liderNome;
    private final List<Membro> membros;
    private final int totalMembros;
    private final LocalDate dataRelatorio;  // Nova campo: data de geração do relatório

    public CelulaReportDTO(Celula celula, List<Membro> membros) {
        this.celulaNome = celula.getNome();

        // Proteção contra líder nulo
        this.liderNome = (celula.getLider() != null)
                ? celula.getLider().getNome()
                : "Sem líder designado";

        this.membros = membros;
        this.totalMembros = membros != null ? membros.size() : 0;
        this.dataRelatorio = LocalDate.now();  // Data atual do sistema
    }
}