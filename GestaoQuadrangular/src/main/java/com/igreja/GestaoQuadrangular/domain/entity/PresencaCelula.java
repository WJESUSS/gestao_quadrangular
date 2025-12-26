package com.igreja.GestaoQuadrangular.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "presenca_celula")
public class PresencaCelula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "celula_id", nullable = false)
    private Celula celula;

    @Column(nullable = false)
    private LocalDate data;

    @ManyToMany
    @JoinTable(
            name = "presenca_celula_membro",
            joinColumns = @JoinColumn(name = "presenca_celula_id"),
            inverseJoinColumns = @JoinColumn(name = "membro_id")
    )
    private Set<Membro> presentes = new HashSet<>();

    // ========================================
    // Construtores
    // ========================================

    public PresencaCelula() {
        // Construtor vazio exigido pelo JPA
    }

    public PresencaCelula(Celula celula, LocalDate data) {
        this.celula = celula;
        this.data = data;
    }

    // ========================================
    // Getters e Setters
    // ========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Celula getCelula() {
        return celula;
    }

    public void setCelula(Celula celula) {
        this.celula = celula;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Set<Membro> getPresentes() {
        return presentes;
    }

    public void setPresentes(Set<Membro> presentes) {
        this.presentes = presentes;
    }

    // ========================================
    // Métodos auxiliares (opcional, mas útil)
    // ========================================

    /**
     * Adiciona um membro à lista de presentes.
     */
    public void adicionarPresente(Membro membro) {
        if (membro != null) {
            this.presentes.add(membro);
        }
    }

    /**
     * Remove um membro da lista de presentes.
     */
    public void removerPresente(Membro membro) {
        if (membro != null) {
            this.presentes.remove(membro);
        }
    }

    /**
     * Limpa todos os registros de presença (útil ao sobrescrever uma reunião).
     */
    public void limparPresentes() {
        this.presentes.clear();
    }

    /**
     * Retorna a quantidade de membros presentes.
     */
    public int getTotalPresentes() {
        return presentes.size();
    }
}