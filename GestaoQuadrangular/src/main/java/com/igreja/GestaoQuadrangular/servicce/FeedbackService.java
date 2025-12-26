package com.igreja.GestaoQuadrangular.servicce;

import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.num.StatusEspiritual;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FeedbackService {


    private final MembroService membroService;
    private final EmailService emailService;  // seu serviço de e-mail existente

    public FeedbackService(MembroService membroService, EmailService emailService) {
           this.membroService = membroService;
        this.emailService = emailService;
    }
    // private final WhatsAppService whatsAppService; // se implementar integração WhatsApp



    // Roda todo dia às 8h da manhã
    @Scheduled(cron = "0 0 8 * * *")
    public void enviarFeedbackAutomatico() {
        LocalDate hoje = LocalDate.now();
        int ano = hoje.getYear();
        int mes = hoje.getMonthValue();

        List<Membro> membros = membroService.listarTodos();

        for (Membro membro : membros) {
            if (membro.isArquivado()) continue; // ignora arquivados

            long faltas = membroService.repository.contarFaltasDomingoNoMes(membro.getId(), ano, mes);
            StatusEspiritual statusAtual = membro.getStatus(); // ou recalcula aqui

            String mensagem = "";

            if (faltas == 2 && statusAtual == StatusEspiritual.AMARELO) {
                mensagem = "Olá " + membro.getNome() + ", Paz seja contigo ! Notamos que você não pôde estar conosco em 2 domingos deste mês. Sentimos sua falta! Estamos orando por você. Se precisar conversar, estou aqui. Abraços, Pr. [Seu Nome]";
            } else if (faltas >= 3 && statusAtual == StatusEspiritual.VERMELHO) {
                mensagem = "Querido(a) " + membro.getNome() + ", A paz seja contigo! Percebi que você não tem vindo aos cultos de domingo nas últimas semanas. A igreja sente sua ausência e eu me preocupo com você. Está tudo bem? Gostaria muito de conversar e orar juntos. Conte comigo! Com carinho, Pr. [Seu Nome]";
            }

            if (!mensagem.isEmpty()) {
                // Envia por e-mail (você já tem EmailService)
                emailService.enviarEmail(
                        membro.getEmail(),
                        "Uma mensagem do seu Pastor",
                        mensagem
                );
       ;
                // Futuro: integração WhatsApp (ex: Twilio, Evolution API ou WhatsApp Business API)
                // whatsAppService.enviar(membro.getTelefone(), mensagem);
            }
        }
    }
}
