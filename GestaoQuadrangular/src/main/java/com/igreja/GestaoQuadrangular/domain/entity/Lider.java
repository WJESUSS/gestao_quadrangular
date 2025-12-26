package com.igreja.GestaoQuadrangular.domain.entity;

import jakarta.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "lideres",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "usuario_id")
        }
)
public class Lider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 20)
    private String telefone;

    // Relação 1:1 obrigatória com Usuario
    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", unique = true, nullable = false)
    private Usuario usuario;

    // Relação bidirecional: Um líder pode liderar várias células
    @OneToMany(
            mappedBy = "lider",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<Celula> celulas = new HashSet<>();

    // ==================== Construtores ====================

    public Lider() {
        this.celulas = new HashSet<>();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Retorna o username do líder a partir da entidade Usuario associada.
     * Útil para autenticação/autorização (ex: verificar se o usuário logado é líder de uma célula).
     */
    public String getUsername() {
        return usuario != null ? usuario.getUsername() : null;
    }

    /**
     * Retorna uma visão imutável da coleção de células.
     * Evita modificações externas acidentais.
     */
    public Set<Celula> getCelulas() {
        return Collections.unmodifiableSet(celulas);
    }

    public void setCelulas(Set<Celula> celulas) {
        this.celulas.clear();
        if (celulas != null) {
            celulas.forEach(this::adicionarCelula);
        }
    }

    // ==================== Métodos auxiliares de relação ====================

    public void adicionarCelula(Celula celula) {
        if (celula == null) return;
        celulas.add(celula);
        celula.setLider(this);
    }

    public void removerCelula(Celula celula) {
        if (celula == null) return;
        celulas.remove(celula);
        celula.setLider(null);
    }

    // ==================== Métodos utilitários ====================

    @Override
    public String toString() {
        return "Lider{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", username='" + getUsername() + '\'' +
                ", qtdCelulas=" + celulas.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lider lider = (Lider) o;
        return Objects.equals(id, lider.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}