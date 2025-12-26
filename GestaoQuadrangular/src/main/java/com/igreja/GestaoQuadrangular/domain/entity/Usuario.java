package com.igreja.GestaoQuadrangular.domain.entity;

import com.igreja.GestaoQuadrangular.num.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 150)
    private String sobrenome;  // ← sobrenome separado (muito útil)

    @Column(length = 50)
    private String titulo;     // ← Pr., Pastora, Bispo, etc.

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(length = 20)
    private String telefone;

    // ==================== Getters e Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    // ==================== Métodos de conveniência (exibição) ====================

    /**
     * Nome principal (primeiro nome ou nome simples)
     */
    public String getNomePrincipal() {
        return nome != null ? nome.trim() : "Usuário sem nome";
    }

    /**
     * Nome completo formatado, priorizando título + nome + sobrenome
     * Exemplos:
     * - "Pr. João Silva"
     * - "Pastora Ana Paula"
     * - "Marcos Oliveira" (sem título)
     */
    public String getNomeCompleto() {
        StringBuilder sb = new StringBuilder();

        // Título (se existir)
        if (titulo != null && !titulo.trim().isEmpty()) {
            sb.append(titulo.trim()).append(" ");
        }

        // Nome principal
        if (nome != null && !nome.trim().isEmpty()) {
            sb.append(nome.trim());
        }

        // Sobrenome (se existir)
        if (sobrenome != null && !sobrenome.trim().isEmpty()) {
            if (sb.length() > 0 && !sb.toString().endsWith(" ")) {
                sb.append(" ");
            }
            sb.append(sobrenome.trim());
        }

        String resultado = sb.toString().trim();
        return resultado.isEmpty() ? "Nome não informado" : resultado;
    }

    // ==================== Implementação de UserDetails ====================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}