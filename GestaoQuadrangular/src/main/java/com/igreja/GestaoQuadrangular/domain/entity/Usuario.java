package com.igreja.GestaoQuadrangular.domain.entity;

import com.igreja.GestaoQuadrangular.num.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "usuarios")
@PrimaryKeyJoinColumn(name = "membro_id") // Faz o vínculo com a tabela 'membros'
public class Usuario extends Membro implements UserDetails {

    // CAMPOS EXCLUSIVOS DE USUÁRIO (NÃO REPETIR O QUE JÁ TEM NO PAI)

    @Column(length = 150)
    private String sobrenome;

    @Column(length = 50)
    private String titulo;

    @NotBlank(message = "Senha é obrigatória")
    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ==================== Getters e Setters ====================
    // Note: getNome, getEmail, etc, são herdados de Membro.java automaticamente.

    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    // ==================== Lógica de Nome ====================
    @Override
    public String getNomeCompleto() {
        StringBuilder sb = new StringBuilder();
        if (titulo != null && !titulo.trim().isEmpty()) sb.append(titulo.trim()).append(" ");
        if (getNome() != null && !getNome().trim().isEmpty()) sb.append(getNome().trim());
        if (sobrenome != null && !sobrenome.trim().isEmpty()) sb.append(" ").append(sobrenome.trim());
        return sb.toString().trim().isEmpty() ? "Nome não informado" : sb.toString().trim();
    }

    // ==================== UserDetails (Segurança) ====================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() { return senha; }

    @Override
    public String getUsername() { return getEmail(); } // Busca o email da classe Membro

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return isAtivo(); } // Usa o campo 'ativo' da classe Membro
}