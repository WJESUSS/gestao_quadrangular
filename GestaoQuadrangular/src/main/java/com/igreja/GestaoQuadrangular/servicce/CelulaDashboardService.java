package com.igreja.GestaoQuadrangular.servicce;

import com.igreja.GestaoQuadrangular.application.dto.CelulaDashboardDTO;
import com.igreja.GestaoQuadrangular.application.dto.MembroRiscoDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.entity.Presenca;
import com.igreja.GestaoQuadrangular.domain.repository.CelulaRepository;
import com.igreja.GestaoQuadrangular.domain.repository.MembroRepository;
import com.igreja.GestaoQuadrangular.domain.repository.PresencaRepository;
import com.igreja.GestaoQuadrangular.num.StatusEspiritual;
import com.igreja.GestaoQuadrangular.num.TipoReuniao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class CelulaDashboardService {

    private final CelulaRepository celulaRepository;
    private final MembroRepository membroRepository;
    private final PresencaRepository presencaRepository;

    public CelulaDashboardService(CelulaRepository celulaRepository,
                                  MembroRepository membroRepository,
                                  PresencaRepository presencaRepository) {
        this.celulaRepository = celulaRepository;
        this.membroRepository = membroRepository;
        this.presencaRepository = presencaRepository;
    }

    @Transactional(readOnly = true)
    public CelulaDashboardDTO getDashboard(Long celulaId) {
        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada com ID: " + celulaId));

        LocalDate hoje = LocalDate.now();
        YearMonth mesAtual = YearMonth.from(hoje);
        LocalDate inicioMes = mesAtual.atDay(1);
        LocalDate fimMes = mesAtual.atEndOfMonth();

        // Membros ativos da célula
        List<Membro> membrosAtivos = membroRepository.findByCelulaIdAndArquivadoFalse(celulaId);
        int totalMembros = membrosAtivos.size();

        // === CÁLCULO DE PRESENÇA NOS CULTOS DE DOMINGO DO MÊS ATUAL ===
        long totalDomingosNoMes = contarDomingosNoMes(mesAtual);
        long totalPresencasEsperadas = totalMembros * totalDomingosNoMes;

        long totalPresencasDomingo = membrosAtivos.stream()
                .mapToLong(membro -> presencaRepository.findByMembroIdAndTipoReuniaoInAndDataBetween(
                                membro.getId(),
                                List.of(TipoReuniao.CULTO_DOMINGO_MANHA, TipoReuniao.CULTO_DOMINGO_NOITE),
                                inicioMes,
                                fimMes
                        ).stream()
                        .filter(Presenca::isPresente)
                        .count())
                .sum();

        double mediaPresencaDomingo = totalPresencasEsperadas > 0
                ? Math.round((double) totalPresencasDomingo / totalPresencasEsperadas * 1000.0) / 10.0
                : 0.0;

        // === CONTAGEM POR STATUS ESPIRITUAL ===
        long verde = membrosAtivos.stream().filter(m -> m.getStatus() == StatusEspiritual.VERDE).count();
        long amarelo = membrosAtivos.stream().filter(m -> m.getStatus() == StatusEspiritual.AMARELO).count();
        long vermelho = membrosAtivos.stream().filter(m -> m.getStatus() == StatusEspiritual.VERMELHO).count();

        // === MEMBROS EM RISCO (AMARELO OU VERMELHO) ===
        List<MembroRiscoDTO> membrosEmRisco = membrosAtivos.stream()
                .filter(m -> m.getStatus() == StatusEspiritual.AMARELO || m.getStatus() == StatusEspiritual.VERMELHO)
                .map(m -> {
                    YearMonth mesAnoAtual = YearMonth.now();
                    int ano = mesAnoAtual.getYear();        // 2025
                    int mes = mesAnoAtual.getMonthValue();
                    int faltas = (int) membroRepository.contarFaltasDomingoNoMes(m.getId(), ano, mes);
                    return MembroRiscoDTO.criarComMensagem(
                            m.getId(),
                            m.getNome(),
                            m.getTelefone(),
                            m.getEmail(),
                            m.getStatus(),
                            faltas
                    );
                })
                .sorted((a, b) -> Integer.compare(b.faltasDomingoNoMes(), a.faltasDomingoNoMes()))
                .toList();

        // === PRÓXIMA REUNIÃO DA CÉLULA ===
        LocalDate proximaReuniao = calcularProximaReuniao(celula.getDiaSemana());

        // === OUTRAS MÉTRICAS (exemplos - você pode implementar conforme sua necessidade) ===
        int presentesUltimaReuniao = 0; // implementar com base na última reunião registrada
        double mediaFrequencia3Meses = 85.0; // calcular média dos últimos 3 meses
        int visitantesUltimoMes = 5; // contar visitantes registrados
        int relatoriosPendentes = 1; // se o relatório deste mês ainda não foi enviado

        return new CelulaDashboardDTO(
                celula.getId(),
                celula.getNome(),
                celula.getDiaSemana(),
                celula.getHorario(),
                celula.getEndereco(),
                celula.getLider() != null ? celula.getLider().getNome() : "Sem líder",
                totalMembros,
                presentesUltimaReuniao,
                mediaFrequencia3Meses,
                visitantesUltimoMes,
                relatoriosPendentes,
                proximaReuniao

        );
    }

    private long contarDomingosNoMes(YearMonth yearMonth) {
        LocalDate primeiroDia = yearMonth.atDay(1);
        LocalDate ultimoDia = yearMonth.atEndOfMonth();
        return primeiroDia.datesUntil(ultimoDia.plusDays(1))
                .filter(data -> data.getDayOfWeek() == DayOfWeek.SUNDAY)
                .count();
    }

    private LocalDate calcularProximaReuniao(String diaSemana) {
        // Converte string para DayOfWeek (ex: "SEGUNDA" -> MONDAY)
        DayOfWeek dia = switch (diaSemana.toUpperCase()) {
            case "SEGUNDA" -> DayOfWeek.MONDAY;
            case "TERCA", "TERÇA" -> DayOfWeek.TUESDAY;
            case "QUARTA" -> DayOfWeek.WEDNESDAY;
            case "QUINTA" -> DayOfWeek.THURSDAY;
            case "SEXTA" -> DayOfWeek.FRIDAY;
            case "SABADO", "SÁBADO" -> DayOfWeek.SATURDAY;
            case "DOMINGO" -> DayOfWeek.SUNDAY;
            default -> DayOfWeek.MONDAY;
        };

        return LocalDate.now().with(TemporalAdjusters.nextOrSame(dia));
    }
}