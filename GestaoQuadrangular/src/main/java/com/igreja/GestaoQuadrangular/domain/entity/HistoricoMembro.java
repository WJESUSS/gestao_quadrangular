package com.igreja.GestaoQuadrangular.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "historico_membros")
@Getter
@Setter
public class HistoricoMembro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membro_id", nullable = false)
    private Membro membro;

    @Column(nullable = false)
    private LocalDate dataEvento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEventoHistorico tipoEvento;

    private String descricao; // ex: "Batizado na água", "Nomeado diácono", "Disciplinado por 30 dias"

    public enum TipoEventoHistorico {
        BATISMO_AGUA,
        BATISMO_ESPRITO_SANTO,
        ENTRADA_IGREJA,
        ENTRADA_CELULA,
        CARGO_RECEBIDO,
        CARGO_RETIRADO,
        CURSO_CONCLUIDO,
        DISCIPLINA_APLICADA,
        DISCIPLINA_REVOGADA,
        OUTRO
    }
}