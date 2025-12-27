package com.igreja.GestaoQuadrangular.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.igreja.GestaoQuadrangular.num.EscadaSucesso;
import com.igreja.GestaoQuadrangular.num.StatusEspiritual;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "membros")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Membro {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 20)
    private String telefone;

    @Column(unique = true, length = 255)
    private String email;

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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setArquivado(boolean arquivado) {
        this.arquivado = arquivado;
    }

    public int getFaltasConsecutivas() {
        return faltasConsecutivas;
    }

    public void setFaltasConsecutivas(int faltasConsecutivas) {
        this.faltasConsecutivas = faltasConsecutivas;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public StatusEspiritual getStatus() {
        return status;
    }

    public void setStatus(StatusEspiritual status) {
        this.status = status;
    }

    public String getDocumentos() {
        return documentos;
    }

    public void setDocumentos(String documentos) {
        this.documentos = documentos;
    }

    public EscadaSucesso getEscadaSucesso() {
        return escadaSucesso;
    }

    public LocalDate getDataUltimaAtualizacaoEscada() {
        return dataUltimaAtualizacaoEscada;
    }

    public void setDataUltimaAtualizacaoEscada(LocalDate dataUltimaAtualizacaoEscada) {
        this.dataUltimaAtualizacaoEscada = dataUltimaAtualizacaoEscada;
    }

    public String getObservacaoDiscipulado() {
        return observacaoDiscipulado;
    }

    public void setObservacaoDiscipulado(String observacaoDiscipulado) {
        this.observacaoDiscipulado = observacaoDiscipulado;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public LocalDate getDataEntradaCelula() {
        return dataEntradaCelula;
    }

    public void setDataEntradaCelula(LocalDate dataEntradaCelula) {
        this.dataEntradaCelula = dataEntradaCelula;
    }

    public LocalDate getDataBatismo() {
        return dataBatismo;
    }

    public void setDataBatismo(LocalDate dataBatismo) {
        this.dataBatismo = dataBatismo;
    }

    public Boolean getSobDisciplina() {
        return sobDisciplina;
    }

    public void setSobDisciplina(Boolean sobDisciplina) {
        this.sobDisciplina = sobDisciplina;
    }

    public Celula getCelula() {
        return celula;
    }

    public void setCelula(Celula celula) {
        this.celula = celula;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDate getDataUltimaPresenca() {
        return dataUltimaPresenca;
    }

    public void setDataUltimaPresenca(LocalDate dataUltimaPresenca) {
        this.dataUltimaPresenca = dataUltimaPresenca;
    }

    public Set<TurmaEscolaBiblica> getTurmasInscritas() {
        return turmasInscritas;
    }

    public void setTurmasInscritas(Set<TurmaEscolaBiblica> turmasInscritas) {
        this.turmasInscritas = turmasInscritas;
    }

    public List<HistoricoMembro> getHistorico() {
        return historico;
    }

    public void setHistorico(List<HistoricoMembro> historico) {
        this.historico = historico;
    }

    public Membro getMae() {
        return mae;
    }

    public void setMae(Membro mae) {
        this.mae = mae;
    }

    public Membro getPai() {
        return pai;
    }

    public void setPai(Membro pai) {
        this.pai = pai;
    }

    @Column(nullable = false)
    private boolean arquivado = false; // primitivo com valor padrão

    @Column(name = "faltas_consecutivas")
    private int faltasConsecutivas = 0;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_espiritual", nullable = false)
    private StatusEspiritual status = StatusEspiritual.VERDE;

    @Column(name = "documentos_json")
    private String documentos;

    @Enumerated(EnumType.STRING)
    @Column(name = "escada_sucesso")
    private EscadaSucesso escadaSucesso = EscadaSucesso.GANHAR;

    @Column(name = "data_ultima_atualizacao_escada")
    private LocalDate dataUltimaAtualizacaoEscada;

    @Column(name = "observacao_discipulado", length = 1000)
    private String observacaoDiscipulado;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "data_entrada_celula")
    private LocalDate dataEntradaCelula;

    @Column(name = "data_batismo")
    private LocalDate dataBatismo;

    @Column(name = "sob_disciplina")
    private Boolean sobDisciplina = false;

    // ==================== CONSTANTES ====================
    private static final int IDADE_MINIMA_VOTACAO = 18;
    private static final int MAX_FALTAS_CONSECUTIVAS = 6;
    private static final EscadaSucesso ETAPA_MINIMA_VOTACAO = EscadaSucesso.CONSOLIDAR;

    // ==================== RELAÇÕES ====================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "celula_id")
    private Celula celula;

    @Column(name = "ativo")
    private Boolean ativo;

    @Column(name = "data_ultima_presenca")
    private LocalDate dataUltimaPresenca;

    @JsonIgnore
    @ManyToMany(mappedBy = "inscritos", fetch = FetchType.LAZY)
    private Set<TurmaEscolaBiblica> turmasInscritas = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "membro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HistoricoMembro> historico = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mae_id")
    @JsonIgnoreProperties({"membros", "pai", "mae"})
    private Membro mae;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pai_id")
    @JsonIgnoreProperties({"membros", "pai", "mae"})
    private Membro pai;

    // ==================== MÉTODOS PERSONALIZADOS ====================

    public void atualizarEscadaSucesso(EscadaSucesso novaEtapa) {
        this.escadaSucesso = novaEtapa;
        this.dataUltimaAtualizacaoEscada = LocalDate.now();
    }


    public void setEscadaSucesso(EscadaSucesso escadaSucesso) {
        this.escadaSucesso = escadaSucesso;
        this.dataUltimaAtualizacaoEscada = LocalDate.now();
    }

    public void definirDataEntradaNaCelula() {
        this.dataEntradaCelula = LocalDate.now();
    }

    public boolean isAptoParaVotar() {
        if (arquivado || Boolean.TRUE.equals(sobDisciplina)) {
            return false;
        }
        if (dataBatismo == null) {
            return false;
        }
        if (dataNascimento == null ||
                Period.between(dataNascimento, LocalDate.now()).getYears() < IDADE_MINIMA_VOTACAO) {
            return false;
        }
        if (faltasConsecutivas > MAX_FALTAS_CONSECUTIVAS) {
            return false;
        }
        if (escadaSucesso == null || escadaSucesso.ordinal() < ETAPA_MINIMA_VOTACAO.ordinal()) {
            return false;
        }

        return true;
    }

    public int getIdade() {
        if (dataNascimento == null) {
            return 0;
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public String getNomeCompleto() {
        return (nome != null && !nome.trim().isEmpty())
                ? nome.trim()
                : "Nome não informado";
    }

    public String getStatusEspiritualIconPath() {
        if (status == null) {
            return "icons/status-desconhecido.png";
        }
        return switch (status) {
            case VERDE    -> "icons/status-verde.png";
            case AMARELO  -> "icons/status-amarelo.png";
            case VERMELHO -> "icons/status-vermelho.png";
        };
    }

    public String getStatusEspiritualNome() {
        return status != null ? status.name().toLowerCase() : "desconhecido";
    }

    public String getStatusEspiritualColor() {
        return status != null ? status.getHexColor() : "#6c757d";
    }

    public String getStatusEspiritualTexto() {
        return status != null ? status.getDescricao() : "NÃO INFORMADO";
    }

    public boolean isAtivo() {
        return Boolean.TRUE.equals(ativo);
    }

    public boolean isSobDisciplina() {
        return Boolean.TRUE.equals(sobDisciplina);
    }

    // ==================== MÉTODOS PARA ARQUIVADO (CORRIGIDOS) ====================

    /**
     * Getter personalizado para compatibilidade com JSON e lógicas externas
     */
    public Boolean getArquivado() {
        return arquivado; // já é boolean primitivo com valor padrão false
    }

    /**
     * Setter personalizado para aceitar Boolean (wrapper) mas manter o primitivo
     */
    public void setArquivado(Boolean arquivado) {
        this.arquivado = Boolean.TRUE.equals(arquivado);
    }

    /**
     * Método isArquivado() - convenção JavaBeans para boolean primitivo
     */
    public boolean isArquivado() {
        return arquivado;
    }
}