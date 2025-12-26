package com.igreja.GestaoQuadrangular.application.dto;

import com.igreja.GestaoQuadrangular.num.StatusEspiritual;

public class MembroResponseDTO {

    private final Long id;
    private final String nome;
    private final String telefone;
    private final String email;
    private final int faltasConsecutivas;
    private final boolean arquivado;
    private final StatusEspiritual status;
    private final Long celulaId;
    private final String celulaNome;

    // CONSTRUTOR OBRIGATÓRIO PARA O HIBERNATE
    public MembroResponseDTO(Long id, String nome, String telefone, String email,
                             int faltasConsecutivas, boolean arquivado, StatusEspiritual status,
                             Long celulaId, String celulaNome) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.faltasConsecutivas = faltasConsecutivas;
        this.arquivado = arquivado;
        this.status = status;
        this.celulaId = celulaId;
        this.celulaNome = celulaNome;
    }

    // Getters (não precisa de setters se for imutável)
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public int getFaltasConsecutivas() { return faltasConsecutivas; }
    public boolean isArquivado() { return arquivado; }
    public StatusEspiritual getStatus() { return status; }
    public Long getCelulaId() { return celulaId; }
    public String getCelulaNome() { return celulaNome; }
}