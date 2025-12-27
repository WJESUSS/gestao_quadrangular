package com.igreja.GestaoQuadrangular.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Adicionado
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "celulas")
public class Celula {

    // CORREÇÃO 1: Removido o campo "private Celula celula" que estava antes do @Id.
    // Ele criava uma relação da Célula com ela mesma (auto-referência) desnecessária
    // e estava usando o mesmo nome de coluna "celula_id" que o JPA usa para os membros.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nome;

    @Column(length = 255)
    private String endereco;

    @Column(length = 50)
    private String diaSemana;

    @Column(length = 20)
    private String horario;

    @Column(name = "casa_de_paz")
    private boolean casaDePaz = false;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "alerta_multiplicacao_enviado")
    private Boolean alertaMultiplicacaoEnviado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lider_id", nullable = false)
    // CORREÇÃO 2: Evita erro de Proxy do Hibernate ao serializar para JSON
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Lider lider;

    @OneToMany(
            mappedBy = "celula",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    // CORREÇÃO 3: IMPORTANTE! Evita o loop infinito (Recursão)
    // Impede que o JSON tente carregar: Celula -> Membro -> Celula -> Membro...
    @JsonIgnoreProperties("celula")
    private Set<Membro> membros = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (this.dataCriacao == null) {
            this.dataCriacao = LocalDateTime.now();
        }
    }

    // ==================== Getters e Setters ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public boolean isAlertaMultiplicacaoEnviado() {
        return alertaMultiplicacaoEnviado != null && alertaMultiplicacaoEnviado;
    }

    public void setAlertaMultiplicacaoEnviado(boolean alertaMultiplicacaoEnviado) {
        this.alertaMultiplicacaoEnviado = alertaMultiplicacaoEnviado;
    }

    public Boolean getAlertaMultiplicacaoEnviado() {
        return alertaMultiplicacaoEnviado;
    }

    public void marcarAlertaEnviado() {
        this.alertaMultiplicacaoEnviado = true;
    }

    public void resetarAlertaMultiplicacao() {
        this.alertaMultiplicacaoEnviado = false;
    }

    public Lider getLider() {
        return lider;
    }

    public void setLider(Lider lider) {
        this.lider = lider;
    }

    public Set<Membro> getMembros() {
        return membros;
    }

    public void setMembros(Set<Membro> membros) {
        this.membros.clear();
        if (membros != null) {
            membros.forEach(this::adicionarMembro);
        }
    }

    public void adicionarMembro(Membro membro) {
        if (membro == null) return;
        membros.add(membro);
        membro.setCelula(this);
    }

    public void removerMembro(Membro membro) {
        if (membro == null) return;
        membros.remove(membro);
        membro.setCelula(null);
    }

    public int getQuantidadeMembros() {
        return membros.size();
    }

    @Override
    public String toString() {
        return "Celula{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", diaSemana='" + diaSemana + '\'' +
                ", horario='" + horario + '\'' +
                ", casaDePaz=" + casaDePaz +
                ", qtdMembros=" + (membros != null ? membros.size() : 0) +
                ", dataCriacao=" + dataCriacao +
                ", alertaMultiplicacaoEnviado=" + alertaMultiplicacaoEnviado +
                '}';
    }
}