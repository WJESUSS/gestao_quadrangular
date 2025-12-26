package com.igreja.GestaoQuadrangular.application.dto;

import com.igreja.GestaoQuadrangular.domain.entity.Visitante;

import java.time.LocalDate;

public record VisitanteResponseDTO(
        Long id,
        String nome,
        String telefone,
        String email,
        LocalDate dataPrimeiraVisita,
        Integer visitasCount,
        String status,
        Long celulaId,
        String celulaNome
) {
    public static VisitanteResponseDTO fromEntity(Visitante v) {
        return new VisitanteResponseDTO(
                v.getId(),
                v.getNome(),
                v.getTelefone(),
                v.getEmail(),
                v.getDataPrimeiraVisita(),
                v.getVisitasCount(),
                v.getStatus().name(),
                v.getCelula().getId(),
                v.getCelula().getNome()
        );
    }
}
