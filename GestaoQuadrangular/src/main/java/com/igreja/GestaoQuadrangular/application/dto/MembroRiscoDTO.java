package com.igreja.GestaoQuadrangular.application.dto;

import com.igreja.GestaoQuadrangular.num.StatusEspiritual;

/**
 * DTO usado no dashboard da célula para destacar membros que precisam de acompanhamento.
 * Mostra nome, telefone, status espiritual, quantidade de faltas aos domingos no mês
 * e uma mensagem sugerida pronta para o líder enviar por WhatsApp ou ligação.
 */
public record MembroRiscoDTO(
        Long id,
        String nome,
        String telefone,
        String email,                  // opcional, útil para e-mail automático
        StatusEspiritual status,       // VERDE, AMARELO ou VERMELHO
        int faltasDomingoNoMes,        // quantas faltas aos domingos no mês atual
        String mensagemSugerida        // texto pronto para contato carinhoso
) {
    /**
     * Construtor auxiliar para criar o DTO com mensagem automática baseada nas faltas
     */
    public static MembroRiscoDTO criarComMensagem(
            Long id,
            String nome,
            String telefone,
            String email,
            StatusEspiritual status,
            int faltasDomingoNoMes) {

        String mensagem;

        if (faltasDomingoNoMes >= 3) {
            mensagem = """
                    Querido(a) %s, a paz do Senhor!

                    Notamos que você não tem podido estar conosco nos cultos de domingo nas últimas semanas.
                    Sentimos muita saudade da sua presença e estamos orando por você e pela sua família.

                    Está tudo bem? Gostaríamos muito de conversar, orar juntos ou até fazer uma visita.
                    Você é muito importante para nós!

                    Quando puder, me avise. Estou à disposição.

                    Um grande abraço,
                    [Seu Nome] - Líder da Célula
                    """.formatted(nome.split(" ")[0]);
        } else if (faltasDomingoNoMes == 2) {
            mensagem = """
                    Oi %s, a paz do Senhor!

                    Sentimos sua falta nos últimos dois domingos. Tudo bem por aí?
                    Esperamos você com carinho no próximo culto!

                    Se precisar de algo ou quiser conversar, estou aqui. 🙏

                    Abraços,
                    [Seu Nome] - Líder da Célula
                    """.formatted(nome.split(" ")[0]);
        } else {
            mensagem = "Oi " + nome.split(" ")[0] + ", sentimos sua falta no culto. Estamos orando por você! 🙏";
        }

        return new MembroRiscoDTO(id, nome, telefone, email, status, faltasDomingoNoMes, mensagem);
    }

    /**
     * Retorna a cor Bootstrap associada ao status (para usar no frontend)
     */
    public String getCorBadge() {
        return switch (status) {
            case VERDE -> "success";
            case AMARELO -> "warning";
            case VERMELHO -> "danger";
        };
    }

    /**
     * Retorna ícone sugerido para o status
     */
    public String getIcone() {
        return switch (status) {
            case VERDE -> "✅";
            case AMARELO -> "⚠️";
            case VERMELHO -> "❤️";
        };
    }
}