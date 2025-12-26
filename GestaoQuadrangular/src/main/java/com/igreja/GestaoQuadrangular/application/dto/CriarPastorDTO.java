package com.igreja.GestaoQuadrangular.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CriarPastorDTO(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        String sobrenome,

        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String senha,

        String telefone,

        String titulo,

        @NotNull(message = "Data de ordenação é obrigatória")
        LocalDate dataOrdenacao,

        @NotBlank(message = "Igreja de ordenação é obrigatória")
        String igrejaOrdenacao,

        // Mudamos para boolean primitivo com valor default false
        boolean pastorPrincipal

) {
    // Método auxiliar opcional para compatibilidade com código antigo
    // (não é necessário se você usar diretamente pastorPrincipal())
    public boolean isPastorPrincipal() {
        return pastorPrincipal;
    }
}