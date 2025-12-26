package com.igreja.GestaoQuadrangular.application.dto;

import java.time.LocalDateTime;

/**
 * DTO para listagem de mensagens no chat (exibidas no app)
 */
public class MensagemChatListDTO {

    private String nomeAutor;
    private String texto;
    private LocalDateTime dataEnvio;

    public MensagemChatListDTO() {}

    public MensagemChatListDTO(String nomeAutor, String texto, LocalDateTime dataEnvio) {
        this.nomeAutor = nomeAutor;
        this.texto = texto;
        this.dataEnvio = dataEnvio;
    }

    // Getters
    public String getNomeAutor() { return nomeAutor; }
    public String getTexto() { return texto; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
}