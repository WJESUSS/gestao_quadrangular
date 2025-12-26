package com.igreja.GestaoQuadrangular.servicce;

import com.igreja.GestaoQuadrangular.application.dto.*;
import com.igreja.GestaoQuadrangular.domain.entity.Contribuicao;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.repository.CelulaRepository;
import com.igreja.GestaoQuadrangular.domain.repository.ContribuicaoRepository;
import com.igreja.GestaoQuadrangular.domain.repository.MembroRepository;
import com.igreja.GestaoQuadrangular.num.TipoOferta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TesourariaDashboardService {

    private final ContribuicaoRepository contribuicaoRepository;
    private final MembroRepository membroRepository;
    private final CelulaRepository celulaRepository;

    public TesourariaDashboardService(ContribuicaoRepository contribuicaoRepository,
                                      MembroRepository membroRepository,
                                      CelulaRepository celulaRepository) {
        this.contribuicaoRepository = contribuicaoRepository;
        this.membroRepository = membroRepository;
        this.celulaRepository = celulaRepository;
    }

    @Transactional(readOnly = true)
    public DashboardFinanceiroDTO getDashboard(LocalDate inicio, LocalDate fim) {
        int anoAtual = LocalDate.now().getYear();

        // 1. Total arrecadado no período
        BigDecimal totalArrecadado = contribuicaoRepository.findByDataBetween(inicio, fim).stream()
                .map(Contribuicao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Gráfico de tipos de oferta
        List<TipoOfertaResumoDTO> tiposOferta = Arrays.stream(TipoOferta.values())
                .map(tipo -> {
                    BigDecimal valor = contribuicaoRepository.sumByTipoOfertaAndDataBetween(tipo, inicio, fim);
                    BigDecimal valorSeguro = valor != null ? valor : BigDecimal.ZERO;
                    double percentual = totalArrecadado.compareTo(BigDecimal.ZERO) > 0
                            ? valorSeguro.divide(totalArrecadado, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).doubleValue()
                            : 0.0;
                    return new TipoOfertaResumoDTO(tipo.name(), valorSeguro, Math.round(percentual * 10.0) / 10.0);
                })
                .sorted((a, b) -> b.valor().compareTo(a.valor()))
                .toList();

        // 3. Membros fiéis e infiéis (contribuiu em pelo menos 6 meses do ano atual)
        List<Membro> membrosAtivos = membroRepository.findByArquivadoFalse();
        List<MembroFielDTO> fieis = new ArrayList<>();
        List<MembroFielDTO> infieis = new ArrayList<>();

        for (Membro m : membrosAtivos) {
            long mesesContribuidos = contribuicaoRepository.findByMembroIdAndDataBetween(
                            m.getId(),
                            LocalDate.of(anoAtual, 1, 1),
                            LocalDate.of(anoAtual, 12, 31)
                    ).stream()
                    .map(c -> c.getData().getMonthValue())
                    .distinct()
                    .count();

            boolean fiel = mesesContribuidos >= 6;
            String celulaNome = m.getCelula() != null ? m.getCelula().getNome() : "Sem célula";

            MembroFielDTO dto = new MembroFielDTO(m.getId(), m.getNome(), celulaNome, fiel);
            if (fiel) fieis.add(dto);
            else infieis.add(dto);
        }

        // 4. Arrecadação por célula
        List<ArrecadacaoCelulaDTO> porCelula = celulaRepository.findAll().stream()
                .map(c -> {
                    BigDecimal total = contribuicaoRepository.sumByCelulaIdAndDataBetween(c.getId(), inicio, fim);
                    BigDecimal totalSeguro = total != null ? total : BigDecimal.ZERO;
                    String lider = c.getLider() != null ? c.getLider().getNome() : "Sem líder";
                    return new ArrecadacaoCelulaDTO(c.getId(), c.getNome(), lider, totalSeguro);
                })
                .filter(e -> e.total().compareTo(BigDecimal.ZERO) > 0)
                .sorted((a, b) -> b.total().compareTo(a.total()))
                .toList();

        // 5. Comparativo mensal (últimos 12 meses)
        List<ComparativoMensalDTO> comparativo = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            LocalDate ini = ym.atDay(1);
            LocalDate f = ym.atEndOfMonth();

            BigDecimal valorMes = contribuicaoRepository.findByDataBetween(ini, f).stream()
                    .map(Contribuicao::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            comparativo.add(new ComparativoMensalDTO(
                    ym.format(java.time.format.DateTimeFormatter.ofPattern("MMM/yyyy")),
                    valorMes
            ));
        }

        return new DashboardFinanceiroDTO(
                inicio, fim, totalArrecadado, tiposOferta,
                fieis, infieis, porCelula, comparativo
        );
    }
}