package com.igreja.GestaoQuadrangular.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para criação de uma nova célula pelo pastor.
 * Contém todas as informações necessárias para cadastrar a célula e associá-la a um líder.
 */
@Data
@NoArgsConstructor  // Necessário para deserialização do JSON (Jackson)
@AllArgsConstructor // Útil para testes e criação manual de objetos
public class CelulaCreateDTO {

    private String nome;

    private String endereco;

    private String diaSemana;  // ex: "Terça-feira"

    private String horario;    // ex: "19:30"

    private boolean casaDePaz;

    private Long liderId;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public boolean isCasaDePaz() {
        return casaDePaz;
    }

    public void setCasaDePaz(boolean casaDePaz) {
        this.casaDePaz = casaDePaz;
    }

    public Long getLiderId() {
        return liderId;
    }

    public void setLiderId(Long liderId) {
        this.liderId = liderId;
    }
    // ID do líder responsável pela célula
}