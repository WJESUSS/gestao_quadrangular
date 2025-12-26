package com.igreja.GestaoQuadrangular.application.dto;

import java.time.LocalDate;
import java.util.List;

public record EncontroDTO(
        Long id,
        LocalDate data,
        String tipo,
        int totalParticipantes,
        List<String> nomesParticipantes // ou List<MembroDTO> se preferir mais detalhes
) {}
