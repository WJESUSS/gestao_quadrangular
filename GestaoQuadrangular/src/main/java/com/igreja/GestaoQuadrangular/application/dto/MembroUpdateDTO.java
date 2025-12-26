package com.igreja.GestaoQuadrangular.application.dto;


// ← ESSA LINHA

import com.igreja.GestaoQuadrangular.num.StatusEspiritual;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MembroUpdateDTO(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        String telefone,

        @Email(message = "E-mail inválido")
        String email,

        StatusEspiritual status,

        Integer faltasConsecutivas,

        Boolean arquivado,

        Long celulaId

) {}