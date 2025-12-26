package com.igreja.GestaoQuadrangular.num;

public enum StatusEspiritual {

    VERDE("Verde - Ativo e frequente", "success", "#28a745"),
    AMARELO("Amarelo - Atenção (faltas recentes)", "warning", "#ffc107"),
    VERMELHO("Vermelho - Risco espiritual (afastado)", "danger", "#dc3545");

    private final String descricao;
    private final String bootstrapClass; // útil para badges no frontend
    private final String hexColor;       // útil para gráficos e ícones

    StatusEspiritual(String descricao, String bootstrapClass, String hexColor) {
        this.descricao = descricao;
        this.bootstrapClass = bootstrapClass;
        this.hexColor = hexColor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getBootstrapClass() {
        return bootstrapClass;
    }

    public String getHexColor() {
        return hexColor;
    }

    // Opcional: método para facilitar conversão de string (ex: de formulário)
    public static StatusEspiritual fromString(String texto) {
        if (texto == null) return VERDE;
        return switch (texto.toUpperCase()) {
            case "VERDE" -> VERDE;
            case "AMARELO" -> AMARELO;
            case "VERMELHO" -> VERMELHO;
            default -> VERDE;
        };
    }
}