package com.igreja.GestaoQuadrangular.domain.entity;

import com.igreja.GestaoQuadrangular.num.TipoReuniao;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "presencas",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"membro_id", "tipo_reuniao", "data"})
        }
)
public class Presenca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membro_id", nullable = false)
    private Membro membro;
    // Adicione esse campo temporário (só para compatibilidade com tabela antiga)
    @Column(name = "culto_id", insertable = false, updatable = false)
    private Long cultoId;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_reuniao", nullable = false)
    private TipoReuniao tipoReuniao;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "presente", nullable = false)
    private boolean presente = true;

    // ================== GETTERS E SETTERS ==================

    public Long getId() {
        return id;
    }

    public Membro getMembro() {
        return membro;
    }

    public void setMembro(Membro membro) {
        this.membro = membro;
    }

    public TipoReuniao getTipoReuniao() {
        return tipoReuniao;
    }

    public void setTipoReuniao(TipoReuniao tipoReuniao) {
        this.tipoReuniao = tipoReuniao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public boolean isPresente() {
        return presente;
    }

    public void setPresente(boolean presente) {
        this.presente = presente;
    }
}