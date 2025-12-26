
        package com.igreja.GestaoQuadrangular.servicce; // ← corrigido: "service"

import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.entity.Pastor;
import com.igreja.GestaoQuadrangular.domain.repository.CelulaRepository;
import com.igreja.GestaoQuadrangular.domain.repository.MembroRepository;
import com.igreja.GestaoQuadrangular.domain.repository.PastorRepository;
import com.igreja.GestaoQuadrangular.servicce.EmailService;
import com.igreja.GestaoQuadrangular.servicce.MembroService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MultiplicacaoCelulaService {

    private final EmailService emailService;
    private final MembroRepository membroRepository;
    private final CelulaRepository celulaRepository;
    private final PastorRepository pastorRepository;
    private final MembroService membroService;

    public MultiplicacaoCelulaService(MembroRepository membroRepository,
                                      CelulaRepository celulaRepository,
                                      EmailService emailService,
                                      PastorRepository pastorRepository,
                                      MembroService membroService) {
        this.membroRepository = membroRepository;
        this.celulaRepository = celulaRepository;
        this.emailService = emailService;
        this.pastorRepository = pastorRepository;
        this.membroService = membroService;
    }

    @Scheduled(cron = "0 0 9 * * *") // Todo dia às 9h da manhã
    @Transactional
    public void verificarEEnviarAlertas() {
        List<Celula> celulas = celulaRepository.findAll();

        for (Celula celula : celulas) {
            long membrosAtivos = membroRepository.countByCelulaIdAndArquivadoFalse(celula.getId());

            // Condição: 8 ou mais membros ativos E alerta ainda não enviado
            if (membrosAtivos >= 8 && !celula.isAlertaMultiplicacaoEnviado()) {

                String liderNome = celula.getLider() != null ? celula.getLider().getNome() : "Não definido";

                String mensagem = """
                 Atenção! Alerta de Multiplicação de Célula
                 
                 A célula "%s" atingiu %d membros ativos.
                 
                 Sugestão: Considere multiplicar a célula e treinar um novo líder.
                 Líder atual: %s
                 """.formatted(celula.getNome(), membrosAtivos, liderNome);

                // Busca o pastor principal de forma segura
                Optional<Pastor> pastorOpt = pastorRepository.findByPastorPrincipalTrue();

                if (pastorOpt.isPresent()) {
                    Pastor pastor = pastorOpt.get();
                    String emailPastor = pastor.getEmail(); // método delegado que você já tem

                    if (emailPastor != null && !emailPastor.isBlank()) {
                        emailService.enviarEmail(
                                emailPastor,
                                "Alerta de Multiplicação de Célula",
                                mensagem
                        );
                    }
                } else {
                    // Opcional: logar que não há pastor principal configurado
                    System.out.println("Aviso: Nenhum pastor principal encontrado para enviar alerta de multiplicação.");
                }

                // Marca o alerta como enviado para não repetir
                celula.setAlertaMultiplicacaoEnviado(true);
                celulaRepository.save(celula);
            }
        }
    }
}