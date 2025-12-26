package com.igreja.GestaoQuadrangular.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "apresentacoes_crianca")
public class ApresentacaoCrianca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_crianca", nullable = false, length = 150)
    private String nomeCrianca;

    @Column(name = "data_nascimento_crianca", nullable = false)
    private LocalDate dataNascimentoCrianca;

    @Column(name = "data_apresentacao", nullable = false)
    private LocalDate dataApresentacao;

    // Pais membros cadastrados
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pai_membro_id")
    private Membro pai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mae_membro_id")
    private Membro mae;

    // Pais visitantes (quando não são membros)
    @Column(name = "nome_pai_visitante", length = 150)
    private String nomePaiVisitante;

    @Column(name = "nome_mae_visitante", length = 150)
    private String nomeMaeVisitante;

    @Column(name = "telefone_contato", length = 20)
    private String telefoneContato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pastor_oficiante_id")
    private Pastor pastorOficiante;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;
    @Column(name = "data_cadastro", nullable = false)
    @CreationTimestamp  // ← ESSA LINHA RESOLVE!
    private LocalDate dataCadastro;




    // ── Getters & Setters ────────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeCrianca() { return nomeCrianca; }
    public void setNomeCrianca(String nomeCrianca) { this.nomeCrianca = nomeCrianca; }

    public LocalDate getDataNascimentoCrianca() { return dataNascimentoCrianca; }
    public void setDataNascimentoCrianca(LocalDate dataNascimentoCrianca) { this.dataNascimentoCrianca = dataNascimentoCrianca; }

    public LocalDate getDataApresentacao() { return dataApresentacao; }
    public void setDataApresentacao(LocalDate dataApresentacao) { this.dataApresentacao = dataApresentacao; }

    public Membro getPai() { return pai; }
    public void setPai(Membro pai) { this.pai = pai; }

    public Membro getMae() { return mae; }
    public void setMae(Membro mae) { this.mae = mae; }

    public String getNomePaiVisitante() { return nomePaiVisitante; }
    public void setNomePaiVisitante(String nomePaiVisitante) { this.nomePaiVisitante = nomePaiVisitante; }

    public String getNomeMaeVisitante() { return nomeMaeVisitante; }
    public void setNomeMaeVisitante(String nomeMaeVisitante) { this.nomeMaeVisitante = nomeMaeVisitante; }

    public String getTelefoneContato() { return telefoneContato; }
    public void setTelefoneContato(String telefoneContato) { this.telefoneContato = telefoneContato; }

    public Pastor getPastorOficiante() { return pastorOficiante; }
    public void setPastorOficiante(Pastor pastorOficiante) { this.pastorOficiante = pastorOficiante; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDate getDataCadastro() { return dataCadastro; }

    // ── Métodos auxiliares úteis ─────────────────────────────────────────
    @Transient
    public String getNomePaiExibicao() {
        if (pai != null) {
            return pai.getNomeCompleto();
        }
        return nomePaiVisitante != null && !nomePaiVisitante.trim().isEmpty()
                ? nomePaiVisitante.trim()
                : "Não informado";
    }

    @Transient
    public String getNomeMaeExibicao() {
        if (mae != null) {
            return mae.getNomeCompleto();
        }
        return nomeMaeVisitante != null && !nomeMaeVisitante.trim().isEmpty()
                ? nomeMaeVisitante.trim()
                : "Não informado";
    }

    @Transient
    public boolean isPaisMembros() {
        return pai != null || mae != null;
    }

    @Transient
    public String getNomePastorExibicao() {
        return pastorOficiante != null
                ? pastorOficiante.getNomeCompleto()
                : "Não definido";
    }

    @Transient
    public String getInformacaoApresentacao() {
        return String.format("Apresentação de %s em %s - Pastor: %s",
                nomeCrianca,
                dataApresentacao,
                getNomePastorExibicao());
    }

    // ── Getters & Setters ───────────────────────────────────────────────

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}