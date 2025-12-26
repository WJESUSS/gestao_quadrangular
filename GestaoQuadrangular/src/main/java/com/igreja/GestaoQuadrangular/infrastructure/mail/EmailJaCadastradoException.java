package com.igreja.GestaoQuadrangular.infrastructure.mail;

public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException(String mensagem) {
        super(mensagem);
    }
}
