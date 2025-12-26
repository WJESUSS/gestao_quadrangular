package com.igreja.GestaoQuadrangular.domain.entity;

import com.igreja.GestaoQuadrangular.num.TipoOferta;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contribuicoes")
public class Contribuicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membro_id", nullable = false)
    private Membro membro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOferta tipoOferta;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate data;

    @Column(length = 100)
    private String observacao;

    @Column(name = "numero_relatorio_secretaria")
    private String numeroRelatorioSecretaria; // para marcar via relatório da secretaria

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Membro getMembro() { return membro; }
    public void setMembro(Membro membro) { this.membro = membro; }

    public TipoOferta getTipoOferta() { return tipoOferta; }
    public void setTipoOferta(TipoOferta tipoOferta) { this.tipoOferta = tipoOferta; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public String getNumeroRelatorioSecretaria() { return numeroRelatorioSecretaria; }
    public void setNumeroRelatorioSecretaria(String numeroRelatorioSecretaria) { this.numeroRelatorioSecretaria = numeroRelatorioSecretaria; }
}
