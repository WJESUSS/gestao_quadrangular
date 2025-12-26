package com.igreja.GestaoQuadrangular.web.exception;

public class VisitanteJaCadastradoException extends RuntimeException {

    public VisitanteJaCadastradoException() {
        super("Visitante já cadastrado");
    }
}
