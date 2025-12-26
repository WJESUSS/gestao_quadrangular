package com.igreja.GestaoQuadrangular.servicce; // ← corrigido: "service"

import com.igreja.GestaoQuadrangular.application.dto.DashboardSecretariaDTO;
import com.igreja.GestaoQuadrangular.application.dto.MembroDTO;
import com.igreja.GestaoQuadrangular.domain.entity.*;
import com.igreja.GestaoQuadrangular.domain.repository.*;

import com.igreja.GestaoQuadrangular.num.EscadaSucesso;
import com.igreja.GestaoQuadrangular.num.StatusEspiritual;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SecretariaDashboardService {

    private final MembroRepository membroRepository;
    private final VisitanteRepository visitanteRepository;
    private final CasamentoRepository casamentoRepository;
    private final PresencaRepository presencaRepository;

    public SecretariaDashboardService(MembroRepository membroRepository,
                                      VisitanteRepository visitanteRepository,
                                      CasamentoRepository casamentoRepository,
                                      PresencaRepository presencaRepository) {
        this.membroRepository = membroRepository;
        this.visitanteRepository = visitanteRepository;
        this.casamentoRepository = casamentoRepository;
        this.presencaRepository = presencaRepository;
    }

    public DashboardSecretariaDTO getDashboardData() {
        LocalDate hoje = LocalDate.now();
        LocalDate trintaDiasAtras = hoje.minusDays(30);

        // Aniversariantes da semana (próximos 7 dias, incluindo hoje)
        List<Membro> aniversariantesSemanaRaw = getAniversariantesSemana();
        List<MembroDTO> aniversariantesSemana = aniversariantesSemanaRaw.stream()
                .map(this::toMembroDTO)
                .toList();

        // Aniversariantes do mês atual
        List<Membro> aniversariantesMesRaw = getAniversariantesMesAtual();
        List<MembroDTO> aniversariantesMes = aniversariantesMesRaw.stream()
                .map(this::toMembroDTO)
                .toList();

        // Visitantes recentes
        List<Visitante> visitantesRecentes = visitanteRepository.findByDataPrimeiraVisitaAfter(trintaDiasAtras);
        long totalVisitantes = visitantesRecentes.size();

        // Visitantes pendentes (antigos)
        List<Visitante> pendentes = visitanteRepository.findByDataPrimeiraVisitaBefore(trintaDiasAtras);
        long totalPendentes = pendentes.size();

        // Taxa de retorno
        long retornaram = visitantesRecentes.stream()
                .filter(v -> v.getVisitasCount() != null && v.getVisitasCount() > 1)
                .count();
        double taxaRetorno = totalVisitantes > 0 ? (retornaram * 100.0 / totalVisitantes) : 0.0;

        // Próximos casamentos
        List<Casamento> proximosCasamentos = casamentoRepository.findByDataCasamentoBetween(hoje, hoje.plusDays(60));

        // Dados incompletos
        long semFoto = membroRepository.countByFotoUrlIsNullOrFotoUrlEmpty();
        long semContato = membroRepository.countByEmailIsNullOrTelefoneIsNull();

        // Gráfico simulado
        Map<String, Double> frequenciaMedia = new LinkedHashMap<>();
        for (int i = 2; i >= 0; i--) {
            LocalDate mes = hoje.minusMonths(i);
            String label = mes.getMonth().toString().substring(0, 3).toUpperCase() + "/" + mes.getYear();
            frequenciaMedia.put(label, 80.0 + i * 3.5);
        }

        // Inativos (placeholders)
        long totalMembrosInativos = 0L;
        List<MembroDTO> inativosMaisDeMeses = Collections.emptyList();

        return new DashboardSecretariaDTO(
                aniversariantesSemana,
                aniversariantesMes,
                totalMembrosInativos,
                inativosMaisDeMeses,
                totalVisitantes,
                retornaram,
                taxaRetorno,
                proximosCasamentos,
                semFoto,
                semContato,
                pendentes,
                totalPendentes,
                frequenciaMedia,
                hoje
        );
    }

    private MembroDTO toMembroDTO(Membro membro) {
        if (membro == null) {
            return null;
        }

        String nomeCelula = membro.getCelula() != null ? membro.getCelula().getNome() : null;
        String nomeLiderCelula = (membro.getCelula() != null && membro.getCelula().getLider() != null)
                ? membro.getCelula().getLider().getNome()
                : null;

        return new MembroDTO(
                membro.getId(),
                membro.getNome(),
                membro.getEmail(),
                membro.getTelefone(),
                nomeCelula,
                nomeLiderCelula,
                membro.getDataUltimaAtualizacaoEscada(),
                membro.getEscadaSucesso(),
                membro.getStatus(),
                membro.getFaltasConsecutivas(),
                membro.isArquivado(),
                membro.getObservacaoDiscipulado()
        );
    }

    // ==================== MÉTODOS AUXILIARES PARA ANIVERSARIANTES ====================

    private List<Membro> getAniversariantesSemana() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(7);

        String inicio = hoje.format(DateTimeFormatter.ofPattern("MM-dd"));
        String fim = limite.format(DateTimeFormatter.ofPattern("MM-dd"));

        List<Membro> resultado;

        if (limite.getYear() > hoje.getYear()) {
            // Cruza o ano
            List<Membro> parte1 = membroRepository.findAniversariantesProximos7Dias(inicio, "12-31");
            List<Membro> parte2 = membroRepository.findAniversariantesProximos7Dias("01-01", fim);
            resultado = Stream.concat(parte1.stream(), parte2.stream())
                    .distinct()
                    .toList();
        } else {
            resultado = membroRepository.findAniversariantesProximos7Dias(inicio, fim);
        }

        return resultado.stream()
                .sorted(Comparator.comparingInt(this::diasAteProximoAniversario))
                .toList();
    }

    private List<Membro> getAniversariantesMesAtual() {
        LocalDate hoje = LocalDate.now();
        String inicioMes = hoje.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("MM-dd"));
        String fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth()).format(DateTimeFormatter.ofPattern("MM-dd"));

        List<Membro> resultado = membroRepository.findAniversariantesProximos7Dias(inicioMes, fimMes);

        return resultado.stream()
                .sorted(Comparator.comparingInt(this::diasAteProximoAniversario))
                .toList();
    }

    /**
     * Calcula quantos dias faltam até o próximo aniversário do membro.
     * Se o aniversário já passou este ano, considera o do próximo ano.
     */
    private int diasAteProximoAniversario(Membro m) {
        LocalDate hoje = LocalDate.now();
        LocalDate nascimento = m.getDataNascimento();

        LocalDate proximoAniversario = nascimento.withYear(hoje.getYear());

        if (proximoAniversario.isBefore(hoje) || proximoAniversario.isEqual(hoje)) {
            proximoAniversario = proximoAniversario.plusYears(1);
        }

        return (int) ChronoUnit.DAYS.between(hoje, proximoAniversario);
    }

    // ==============================================================================
}