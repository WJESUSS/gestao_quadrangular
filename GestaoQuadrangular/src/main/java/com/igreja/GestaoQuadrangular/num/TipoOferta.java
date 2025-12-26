package com.igreja.GestaoQuadrangular.num;

public enum TipoOferta {
    DIZIMO("Dízimo"),
    OFERTA_OURO("Oferta Ouro"),
    OFERTA_PRATA("Oferta Prata"),
    OFERTA_BRONZE("Oferta Bronze"),
    OFERTA_ESPECIAL("Oferta Especial"),
    MISSÕES("Missões"),
    CONSTRUÇÃO("Construção");

    private final String descricao;

    TipoOferta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
