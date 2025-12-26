package com.igreja.GestaoQuadrangular.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "turmas_escola_biblica")
@Getter
@Setter
public class TurmaEscolaBiblica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome; // ex: "Crescimento 2026 - Turma A"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTurma tipoTurma;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private String professor;

    private int capacidadeMaxima = 30;

    @ManyToMany(fetch = FetchType.EAGER)  // ← MUDE PARA EAGER
    @JoinTable(
            name = "inscricoes_turmas",
            joinColumns = @JoinColumn(name = "turma_id"),
            inverseJoinColumns = @JoinColumn(name = "membro_id")
    )
    private Set<Membro> inscritos = new HashSet<>();

    public enum TipoTurma {
        NOVOS_CONVERTIDOS,
        CRESCIMENTO,
        DISCIPULO,
        LIDERES_HOMENS,
        LIDERES_MULHERES,
        IDOSOS,
        CRIANCAS
    }

    // Métodos úteis
    public boolean adicionarInscrito(Membro membro) {
        if (inscritos.size() < capacidadeMaxima) {
            return inscritos.add(membro);
        }
        return false;
    }

    public boolean removerInscrito(Membro membro) {
        return inscritos.remove(membro);
    }

    public int getTotalInscritos() {
        return inscritos.size();
    }

    public boolean isLotada() {
        return getTotalInscritos() >= capacidadeMaxima;
    }
}