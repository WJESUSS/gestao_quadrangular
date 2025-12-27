package com.igreja.GestaoQuadrangular.servicce;

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

        // Aniversariantes
        List<MembroDTO> aniversariantesSemana = getAniversariantesSemana().stream()
                .map(this::toMembroDTO)
                .toList();
        List<MembroDTO> aniversariantesMes = getAniversariantesMesAtual().stream()
                .map(this::toMembroDTO)
                .toList();

        // Visitantes
        List<Visitante> visitantesRecentes = visitanteRepository.findByDataPrimeiraVisitaAfter(trintaDiasAtras);
        long totalVisitantes = visitantesRecentes.size();
        List<Visitante> pendentes = visitanteRepository.findByDataPrimeiraVisitaBefore(trintaDiasAtras);
        long totalPendentes = pendentes.size();

        long retornaram = visitantesRecentes.stream()
                .filter(v -> v.getVisitasCount() != null && v.getVisitasCount() > 1)
                .count();
        double taxaRetorno = totalVisitantes > 0 ? (retornaram * 100.0 / totalVisitantes) : 0.0;

        // Próximos casamentos
        List<Casamento> proximosCasamentos = casamentoRepository.findByDataCasamentoBetween(hoje, hoje.plusDays(60));

        // Dados incompletos
        long semFoto = membroRepository.countByFotoUrlIsNullOrFotoUrlEmpty();
        long semContato = membroRepository.countByEmailIsNullOrTelefoneIsNull();

        // Gráfico exemplo
        Map<String, Double> frequenciaMedia = new LinkedHashMap<>();
        for (int i = 2; i >= 0; i--) {
            LocalDate mes = hoje.minusMonths(i);
            String label = mes.getMonth().toString().substring(0,3).toUpperCase() + "/" + mes.getYear();
            frequenciaMedia.put(label, 80.0 + i * 3.5);
        }

        return new DashboardSecretariaDTO(
                aniversariantesSemana,
                aniversariantesMes,
                0L,
                Collections.emptyList(),
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
        return new MembroDTO(
                membro.getId(),
                membro.getNome(),
                membro.getEmail(),
                membro.getTelefone(),
                membro.getCelula() != null ? membro.getCelula().getNome() : null,
                (membro.getCelula() != null && membro.getCelula().getLider() != null)
                        ? membro.getCelula().getLider().getNome() : null,
                membro.getDataUltimaAtualizacaoEscada(),
                membro.getEscadaSucesso(),
                membro.getStatus(),
                membro.getFaltasConsecutivas(),
                membro.isArquivado(),
                membro.getObservacaoDiscipulado()
        );
    }

    // ==================== Métodos auxiliares ====================
    private List<Membro> getAniversariantesSemana() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(7);
        String inicio = hoje.format(DateTimeFormatter.ofPattern("MM-dd"));
        String fim = limite.format(DateTimeFormatter.ofPattern("MM-dd"));

        List<Membro> resultado;
        if (limite.getYear() > hoje.getYear()) {
            resultado = Stream.concat(
                    membroRepository.findAniversariantesProximos7Dias(inicio, "12-31").stream(),
                    membroRepository.findAniversariantesProximos7Dias("01-01", fim).stream()
            ).distinct().toList();
        } else {
            resultado = membroRepository.findAniversariantesProximos7Dias(inicio, fim);
        }

        resultado.sort(Comparator.comparingInt(this::diasAteProximoAniversario));
        return resultado;
    }

    private List<Membro> getAniversariantesMesAtual() {
        LocalDate hoje = LocalDate.now();
        String inicioMes = hoje.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("MM-dd"));
        String fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth()).format(DateTimeFormatter.ofPattern("MM-dd"));

        List<Membro> resultado = membroRepository.findAniversariantesProximos7Dias(inicioMes, fimMes);
        resultado.sort(Comparator.comparingInt(this::diasAteProximoAniversario));
        return resultado;
    }

    private int diasAteProximoAniversario(Membro m) {
        LocalDate hoje = LocalDate.now();
        LocalDate nascimento = m.getDataNascimento();
        LocalDate proximo = nascimento.withYear(hoje.getYear());
        if (!proximo.isAfter(hoje)) proximo = proximo.plusYears(1);
        return (int) ChronoUnit.DAYS.between(hoje, proximo);
    }
}
