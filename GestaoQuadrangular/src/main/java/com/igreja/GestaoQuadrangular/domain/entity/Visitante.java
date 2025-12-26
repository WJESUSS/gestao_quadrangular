package com.igreja.GestaoQuadrangular.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"observacoes"})
public class Visitante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===================== DADOS PESSOAIS =====================

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nome;

    @Size(max = 20)
    @Column(length = 20)
    private String telefone;

    @Email
    @Size(max = 255)
    @Column(length = 255)
    private String email;

    // ===================== STATUS =====================

    @Column(name = "eh_visitante", nullable = false)
    private boolean ehVisitante = true;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private StatusVisitante status = StatusVisitante.NOVO;

    // ===================== RELACIONAMENTOS =====================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "celula_id", nullable = false)
    private Celula celula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por_id")
    private Usuario registradoPor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membro_id")
    private Membro convertidoParaMembro;

    // ===================== DATAS =====================

    /** Data da PRIMEIRA visita (fixa) */
    @NotNull
    @Column(name = "data_primeira_visita", nullable = false)
    private LocalDate dataPrimeiraVisita = LocalDate.now();

    /** Data da VISITA atual (obrigatória no banco) */
    @NotNull
    @Column(name = "data_visita", nullable = false)
    private LocalDate dataVisita;


    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
    @PrePersist
    public void prePersist() {
        if (this.dataPrimeiraVisita == null) {
            this.dataPrimeiraVisita = LocalDate.now();
        }

        if (this.dataVisita == null) {
            this.dataVisita = this.dataPrimeiraVisita;
        }

        if (this.visitasCount == null || this.visitasCount < 1) {
            this.visitasCount = 1;
        }

        if (this.status == null) {
            this.status = StatusVisitante.NOVO;
        }
        if (this.criadoEm == null) {
            this.criadoEm = LocalDateTime.now();
        }
    }

    // ===================== CONTROLE =====================

    @Column(name = "visitas_count", nullable = false)
    private Integer visitasCount = 1;

    @Size(max = 255)
    @Column(length = 255)
    private String origem;

    @Size(max = 100)
    @Column(length = 100)
    private String eventoFrequentado;

    @Size(max = 1000)
    @Column(length = 1000)
    private String observacoes;

    // ===================== REGRAS DE NEGÓCIO =====================

    public void registrarNovaVisita() {
        this.visitasCount++;
        this.dataVisita = LocalDate.now();

        if (this.visitasCount > 1) {
            this.status = StatusVisitante.RECORRENTE;
        }
    }

    public boolean isRecorrente() {
        return visitasCount > 1;
    }

    public void incrementarVisita() {
        // incrementa o número de visitas
        this.visitasCount++;

        // atualiza a data da última visita
        this.dataVisita = LocalDate.now();

        // atualiza o status conforme regra de negócio
        if (this.visitasCount > 1 && this.status == StatusVisitante.NOVO) {
            this.status = StatusVisitante.RECORRENTE;
        }
    }

    // ===================== ENUM =====================

    public enum StatusVisitante {
        NOVO,
        RECORRENTE,
        EM_ACOMPANHAMENTO,
        CONVERTIDO,
        PERDIDO
    }
}
