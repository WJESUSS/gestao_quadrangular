package com.igreja.GestaoQuadrangular.application.dto;

import java.time.LocalDateTime;

/**
 * DTO para envio de mensagem no chat da célula
 */
public class MensagemChatCreateDTO {

    private Long celulaId;
    private String texto;

    public MensagemChatCreateDTO() {}

    public MensagemChatCreateDTO(Long celulaId, String texto) {
        this.celulaId = celulaId;
        this.texto = texto;
    }

    // Getters e Setters
    public Long getCelulaId() { return celulaId; }
    public void setCelulaId(Long celulaId) { this.celulaId = celulaId; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}