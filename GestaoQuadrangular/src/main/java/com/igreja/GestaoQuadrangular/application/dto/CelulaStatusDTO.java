package com.igreja.GestaoQuadrangular.application.dto;


public record CelulaStatusDTO(
        Long id,
        String nome,
        String endereco,
        String diaSemana,
        String horario,
        boolean casaDePaz,
        String liderNome,
        String status,
        int membros,
        int visitantes,
        int totalPresentes,
        int membrosFaltosos
) {

    // Construtor básico: apenas membros informados
    public CelulaStatusDTO(
            Long id,
            String nome,
            String endereco,
            String diaSemana,
            String horario,
            boolean casaDePaz,
            String liderNome,
            String status,
            int membros
    ) {
        this(
                id,
                nome,
                endereco,
                diaSemana,
                horario,
                casaDePaz,
                liderNome,
                status,
                membros,
                0,          // visitantes default
                membros,    // totalPresentes default = todos presentes
                0           // membrosFaltosos default = recalculado depois se necessário
        );
    }

    // Construtor com visitantes e totalPresentes informados
    public CelulaStatusDTO(
            Long id,
            String nome,
            String endereco,
            String diaSemana,
            String horario,
            boolean casaDePaz,
            String liderNome,
            String status,
            int membros,
            int visitantes,
            int totalPresentes
    ) {
        this(
                id,
                nome,
                endereco,
                diaSemana,
                horario,
                casaDePaz,
                liderNome,
                status,
                membros,
                visitantes,
                totalPresentes,
                Math.max(0, membros - (totalPresentes - visitantes))// calculando membros faltosos
        );
    }
}
