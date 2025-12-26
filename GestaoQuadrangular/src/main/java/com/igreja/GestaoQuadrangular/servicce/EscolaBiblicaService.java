package com.igreja.GestaoQuadrangular.servicce;

import com.igreja.GestaoQuadrangular.application.dto.MembroResumoDTO;
import com.igreja.GestaoQuadrangular.application.dto.TurmaEscolaBiblicaDTO;
import com.igreja.GestaoQuadrangular.domain.entity.HistoricoMembro;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.entity.TurmaEscolaBiblica;
import com.igreja.GestaoQuadrangular.domain.repository.HistoricoMembroRepository;
import com.igreja.GestaoQuadrangular.domain.repository.MembroRepository;
import com.igreja.GestaoQuadrangular.domain.repository.TurmaEscolaBiblicaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EscolaBiblicaService {

    private final MembroRepository membroRepository;
    private final TurmaEscolaBiblicaRepository turmaRepository;
    private final HistoricoMembroRepository historicoRepository;

    public EscolaBiblicaService(MembroRepository membroRepository,
                                TurmaEscolaBiblicaRepository turmaRepository,
                                HistoricoMembroRepository historicoRepository) {
        this.membroRepository = membroRepository;
        this.turmaRepository = turmaRepository;
        this.historicoRepository = historicoRepository;
    }

    // ==================== HISTÓRICO DO MEMBRO ====================

    /**
     * Adiciona um evento ao histórico do membro
     */
    public HistoricoMembro adicionarEventoHistorico(Long membroId,
                                                    HistoricoMembro.TipoEventoHistorico tipo,
                                                    String descricao) {
        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new IllegalArgumentException("Membro não encontrado: " + membroId));

        HistoricoMembro evento = new HistoricoMembro();
        evento.setMembro(membro);
        evento.setDataEvento(LocalDate.now());
        evento.setTipoEvento(tipo);
        evento.setDescricao(descricao);

        return historicoRepository.save(evento);
    }

    /**
     * Exemplo de uso: registrar batismo
     */
    public HistoricoMembro registrarBatismo(Long membroId, String pastor) {
        return adicionarEventoHistorico(
                membroId,
                HistoricoMembro.TipoEventoHistorico.BATISMO_AGUA,
                "Batizado nas águas pelo " + pastor
        );
    }

    /**
     * Lista o histórico completo de um membro (ordenado do mais recente para o mais antigo)
     */
    public List<HistoricoMembro> getHistoricoMembro(Long membroId) {
        return historicoRepository.findByMembroIdOrderByDataEventoDesc(membroId);
    }

    // ==================== GESTÃO DE TURMAS E INSCRIÇÕES ====================

    /**
     * Inscreve um membro em uma turma (com validação de capacidade)
     */
    public void inscreverMembroEmTurma(Long membroId, Long turmaId) {
        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new IllegalArgumentException("Membro não encontrado"));

        TurmaEscolaBiblica turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));

        if (turma.isLotada()) {
            throw new IllegalStateException("Turma lotada: " + turma.getNome());
        }

        if (!turma.adicionarInscrito(membro)) {
            throw new IllegalStateException("Não foi possível inscrever o membro (já inscrito ou erro)");
        }

        turmaRepository.save(turma);

        // Registro automático no histórico
        adicionarEventoHistorico(
                membroId,
                HistoricoMembro.TipoEventoHistorico.CURSO_CONCLUIDO,
                "Inscrito na turma: " + turma.getNome() + " (" + turma.getTipoTurma() + ")"
        );
    }

    /**
     * Remove inscrição de um membro de uma turma
     */
    public void removerInscricao(Long membroId, Long turmaId) {
        TurmaEscolaBiblica turma = turmaRepository.findById(turmaId).orElseThrow();
        Membro membro = membroRepository.findById(membroId).orElseThrow();

        if (turma.removerInscrito(membro)) {
            turmaRepository.save(turma);

            adicionarEventoHistorico(
                    membroId,
                    HistoricoMembro.TipoEventoHistorico.OUTRO,
                    "Removido da turma: " + turma.getNome()
            );
        }
    }

    /**
     * Lista todas as turmas abertas (sem data de fim ou com data no futuro)
     */
    public List<TurmaEscolaBiblica> listarTurmasAbertas() {
        return turmaRepository.findByDataFimAfter(LocalDate.now());
    }

    /**
     * Lista turmas por tipo (ex: NOVOS_CONVERTIDOS, LIDERES_HOMENS, etc.)
     */
    public List<TurmaEscolaBiblica> listarTurmasPorTipo(TurmaEscolaBiblica.TipoTurma tipo) {
        return turmaRepository.findByTipoTurma(tipo);
    }

    /**
     * Cria uma nova turma
     */
    public TurmaEscolaBiblica criarTurma(String nome,
                                         TurmaEscolaBiblica.TipoTurma tipo,
                                         LocalDate dataInicio,
                                         LocalDate dataFim,
                                         String professor,
                                         int capacidade) {
        TurmaEscolaBiblica turma = new TurmaEscolaBiblica();
        turma.setNome(nome);
        turma.setTipoTurma(tipo);
        turma.setDataInicio(dataInicio);
        turma.setDataFim(dataFim);
        turma.setProfessor(professor);
        turma.setCapacidadeMaxima(capacidade);

        return turmaRepository.save(turma);
    }

    /**
     * Busca uma turma da Escola Bíblica por ID
     * @param id ID da turma
     * @return TurmaEscolaBiblica encontrada
     * @throws IllegalArgumentException se a turma não existir
     */
    public TurmaEscolaBiblica getTurmaPorId(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada com o ID: " + id));
    }
    public TurmaEscolaBiblicaDTO toTurmaDTO(TurmaEscolaBiblica turma) {
        List<MembroResumoDTO> inscritosResumo = turma.getInscritos().stream()
                .map(m -> new MembroResumoDTO(m.getId(), m.getNome(), m.getTelefone()))
                .toList();

        return new TurmaEscolaBiblicaDTO(
                turma.getId(),
                turma.getNome(),
                turma.getTipoTurma(),
                turma.getDataInicio(),
                turma.getDataFim(),
                turma.getProfessor(),
                turma.getCapacidadeMaxima(),
                turma.getTotalInscritos(),
                turma.isLotada(),
                inscritosResumo
        );
    }

    public List<TurmaEscolaBiblicaDTO> toTurmaDTOList(List<TurmaEscolaBiblica> turmas) {
        return turmas.stream().map(this::toTurmaDTO).toList();
    }
}