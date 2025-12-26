package com.igreja.GestaoQuadrangular.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pastores")
public class Pastor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true, nullable = false)
    private Usuario usuario;

    @Column(name = "data_ordenacao")
    private LocalDate dataOrdenacao;

    @Column(name = "igreja_ordenacao", length = 150)
    private String igrejaOrdenacao;

    @Column(name = "pastor_principal")
    private boolean pastorPrincipal = false;

    // ==============================================
    // GETTERS E SETTERS
    // ==============================================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getDataOrdenacao() {
        return dataOrdenacao;
    }

    public void setDataOrdenacao(LocalDate dataOrdenacao) {
        this.dataOrdenacao = dataOrdenacao;
    }

    public String getIgrejaOrdenacao() {
        return igrejaOrdenacao;
    }

    public void setIgrejaOrdenacao(String igrejaOrdenacao) {
        this.igrejaOrdenacao = igrejaOrdenacao;
    }

    public boolean isPastorPrincipal() {
        return pastorPrincipal;
    }

    public void setPastorPrincipal(boolean pastorPrincipal) {
        this.pastorPrincipal = pastorPrincipal;
    }

    // ==============================================
    // MÉTODOS DE CONVENIÊNCIA (delegam para Usuario)
    // ==============================================

    /**
     * Retorna o nome principal do pastor (normalmente o primeiro nome)
     */
    @Transient
    public String getNome() {
        if (usuario == null) {
            return "Não informado";
        }
        return usuario.getNome() != null
                ? usuario.getNome().trim()
                : "Não informado";
    }

    /**
     * Retorna o nome completo do pastor, incluindo título se houver
     * (muito usado em certificados, atas e apresentações)
     */
    @Transient
    public String getNomeCompleto() {
        if (usuario == null) {
            return "Pastor não associado";
        }

        String nome = usuario.getNome() != null ? usuario.getNome().trim() : "";
        String sobrenome = usuario.getSobrenome() != null ? usuario.getSobrenome().trim() : "";
        String titulo = usuario.getTitulo() != null ? usuario.getTitulo().trim() : "";

        StringBuilder nomeCompleto = new StringBuilder();

        // Adiciona título se existir (Pr., Pastora, Bispo, etc)
        if (!titulo.isEmpty()) {
            nomeCompleto.append(titulo).append(" ");
        }

        // Nome principal
        if (!nome.isEmpty()) {
            nomeCompleto.append(nome);
        }

        // Sobrenome
        if (!sobrenome.isEmpty()) {
            if (!nome.isEmpty()) {
                nomeCompleto.append(" ");
            }
            nomeCompleto.append(sobrenome);
        }

        String resultado = nomeCompleto.toString().trim();
        return resultado.isEmpty() ? "Nome não informado" : resultado;
    }

    /**
     * Email do pastor (delegado do usuário)
     */
    @Transient
    public String getEmail() {
        if (usuario == null) {
            return null;
        }
        return usuario.getEmail() != null ? usuario.getEmail().trim() : null;
    }

    // ==============================================
    // Método auxiliar útil para certificados/apresentações
    // ==============================================
    @Transient
    public String getNomeExibicaoComTitulo() {
        String titulo = usuario != null && usuario.getTitulo() != null
                ? usuario.getTitulo().trim() + " "
                : "";
        return titulo + getNomeCompleto();
    }
}