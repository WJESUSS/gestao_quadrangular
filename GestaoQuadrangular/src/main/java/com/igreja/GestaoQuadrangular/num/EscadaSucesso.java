package com.igreja.GestaoQuadrangular.num;


public enum EscadaSucesso {
    GANHAR("Ganhar - Novo convertido"),
    CONSOLIDAR("Consolidar - Integrado na igreja"),
    DISCIPULAR("Discipular - Treinado"),
    ENVIAR("Enviar - Líder ou missionário");

    private final String descricao;

    EscadaSucesso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}