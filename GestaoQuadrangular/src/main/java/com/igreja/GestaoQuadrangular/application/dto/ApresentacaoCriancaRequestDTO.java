package com.igreja.GestaoQuadrangular.application.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ApresentacaoCriancaRequestDTO(

        @NotBlank(message = "Nome da criança é obrigatório")
        @Size(max = 150)
        String nomeCrianca,

        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve ser no passado")
        LocalDate dataNascimentoCrianca,

        @NotNull(message = "Data da apresentação é obrigatória")
        @Future(message = "A apresentação deve ser no futuro")
        LocalDate dataApresentacao,

        // Pais que são membros (use um ou ambos)
        Long paiMembroId,      // opcional
        Long maeMembroId,      // opcional

        // Pais visitantes (use quando NÃO são membros)
        @Size(max = 150)
        String nomePaiVisitante,

        @Size(max = 150)
        String nomeMaeVisitante,

        // Pastor que irá oficiar (opcional)
        Long pastorOficianteId,

        // Contato principal (muito recomendado)
        @Size(max = 20)
        String telefoneContato,

        // Observações / detalhes extras
        @Size(max = 1000)
        String observacoes
) {
    // Validação de negócio: deve ter pelo menos um pai/mãe informado
    public boolean hasPaiInformado() {
        return paiMembroId != null || nomePaiVisitante != null && !nomePaiVisitante.isBlank();
    }

    public boolean hasMaeInformada() {
        return maeMembroId != null || nomeMaeVisitante != null && !nomeMaeVisitante.isBlank();
    }
}