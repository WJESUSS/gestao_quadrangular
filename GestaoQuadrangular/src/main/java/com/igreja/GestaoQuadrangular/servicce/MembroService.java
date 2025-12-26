package com.igreja.GestaoQuadrangular.servicce;

import com.igreja.GestaoQuadrangular.application.dto.MembroFaltasDomingoDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.repository.MembroRepository;
import com.igreja.GestaoQuadrangular.num.EscadaSucesso;
import com.igreja.GestaoQuadrangular.num.StatusEspiritual;
import com.igreja.GestaoQuadrangular.web.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class MembroService {

    public final MembroRepository repository;

    public MembroService(MembroRepository repository) {
        this.repository = repository;
    }

    /**
     * Calcula o status espiritual com base nas faltas consecutivas
     */
    private StatusEspiritual calcularStatus(int faltas) {
        if (faltas >= 3) {
            return StatusEspiritual.VERMELHO;
        } else if (faltas == 2) {
            return StatusEspiritual.AMARELO;
        } else {
            return StatusEspiritual.VERDE;
        }
    }

    /**
     * (Versão alternativa comentada) Status baseado em faltas nos domingos do mês atual
     */

    public List<MembroFaltasDomingoDTO> gerarRelatorioFaltasDomingoMesAtual() {
        LocalDate hoje = LocalDate.now();
        int ano = hoje.getYear();
        int mes = hoje.getMonthValue();

        List<Membro> todosMembros = repository.findAll();

        return todosMembros.stream()
                .map(m -> {
                    long faltas = repository.contarFaltasDomingoNoMes(m.getId(), ano, mes);
                    StatusEspiritual status = faltas >= 3 ? StatusEspiritual.VERMELHO :
                            faltas == 2 ? StatusEspiritual.AMARELO :
                                    StatusEspiritual.VERDE;

                    String celulaNome = m.getCelula() != null ? m.getCelula().getNome() : "Sem célula";

                    return new MembroFaltasDomingoDTO(
                            m.getId(),
                            m.getNome(),
                            m.getTelefone(),
                            (int) faltas,
                            status,
                            celulaNome
                    );
                })
                .sorted((a, b) -> {
                    // Ordena por mais faltas primeiro
                    return Integer.compare(b.faltasNoMes(), a.faltasNoMes());
                })
                .toList();
    }


    /**
     * Busca um membro por ID ou lança exceção personalizada
     */
    public Membro buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado com id: " + id));
    }

    @Transactional
    public Membro criar(Membro membro) {
        membro.setStatus(calcularStatus(membro.getFaltasConsecutivas()));
        // Se quiser usar a nova lógica: membro.setStatus(calcularStatusPorFaltasDomingoNoMes(membro));
        return repository.save(membro);
    }

    @Transactional
    public Membro atualizar(Long id, Membro dadosAtualizados) {
        Membro existente = buscarPorId(id);

        // Atualiza apenas os campos permitidos
        if (dadosAtualizados.getNome() != null) existente.setNome(dadosAtualizados.getNome());
        if (dadosAtualizados.getTelefone() != null) existente.setTelefone(dadosAtualizados.getTelefone());
        if (dadosAtualizados.getEmail() != null) existente.setEmail(dadosAtualizados.getEmail());
        if (dadosAtualizados.getFaltasConsecutivas() >= 0) {
            existente.setFaltasConsecutivas(dadosAtualizados.getFaltasConsecutivas());
        }

        // Recalcula o status
        existente.setStatus(calcularStatus(existente.getFaltasConsecutivas()));
        // Se quiser nova lógica: existente.setStatus(calcularStatusPorFaltasDomingoNoMes(existente));

        return repository.save(existente);
    }

    @Transactional
    public Membro registrarFalta(Long id) {
        Membro membro = buscarPorId(id);
        int novasFaltas = membro.getFaltasConsecutivas() + 1;
        membro.setFaltasConsecutivas(novasFaltas);
        membro.setStatus(calcularStatus(novasFaltas));
        // Se quiser nova lógica: membro.setStatus(calcularStatusPorFaltasDomingoNoMes(membro));
        return repository.save(membro);
    }

    @Transactional
    public Membro registrarPresenca(Long id) {
        Membro membro = buscarPorId(id);
        membro.setFaltasConsecutivas(0);
        membro.setStatus(StatusEspiritual.VERDE);
        // Se quiser nova lógica: membro.setStatus(calcularStatusPorFaltasDomingoNoMes(membro));
        return repository.save(membro);
    }

    @Transactional
    public Membro toggleArquivado(Long id) {
        Membro membro = buscarPorId(id);
        membro.setArquivado(!membro.isArquivado());
        return repository.save(membro);
    }

    @Transactional
    public Membro arquivar(Long id, boolean arquivar) {
        Membro membro = buscarPorId(id);
        membro.setArquivado(arquivar);
        return repository.save(membro);
    }

    @Transactional(readOnly = true)
    public List<Membro> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Membro buscarPorIdPublico(Long id) {
        return buscarPorId(id);
    }

    @Transactional
    public void deletar(Long id) {
        Membro membro = buscarPorId(id);
        repository.delete(membro);
    }

    @Transactional
    public Membro atualizarFaltas(Long membroId, int novasFaltas) {
        Membro membro = buscarPorId(membroId);
        membro.setFaltasConsecutivas(novasFaltas);
        membro.setStatus(calcularStatus(novasFaltas));
        // Se quiser nova lógica: membro.setStatus(calcularStatusPorFaltasDomingoNoMes(membro));
        return repository.save(membro);
    }

    @Transactional
    public Membro salvar(Membro membro) {
        membro.setStatus(calcularStatus(membro.getFaltasConsecutivas()));
        // Se quiser nova lógica: membro.setStatus(calcularStatusPorFaltasDomingoNoMes(membro));
        return repository.save(membro);
    }


    private static final int IDADE_MINIMA_VOTACAO = 18;
    private static final int MAX_FALTAS_CONSECUTIVAS = 6;
    private static final EscadaSucesso ETAPA_MINIMA_VOTACAO = EscadaSucesso.CONSOLIDAR;

    /**
     * Retorna a lista de membros aptos a votar (ordenada por nome)
     */
    public List<Membro> listarAptosParaVotar() {
        LocalDate dataLimiteIdade = LocalDate.now().minusYears(IDADE_MINIMA_VOTACAO);

        // Usa a versão sem Pageable
        return repository.findMembrosAptosParaVotar(
                dataLimiteIdade,
                MAX_FALTAS_CONSECUTIVAS,
                ETAPA_MINIMA_VOTACAO
        );
    }

    // Se quiser com paginação no futuro:
    public Page<Membro> listarAptosParaVotarPaginado(Pageable pageable) {
        LocalDate dataLimiteIdade = LocalDate.now().minusYears(IDADE_MINIMA_VOTACAO);

        return repository.findMembrosAptosParaVotar(
                dataLimiteIdade,
                MAX_FALTAS_CONSECUTIVAS,
                ETAPA_MINIMA_VOTACAO,
                pageable
        );

    }
    // MembroService
    public List<Membro> getAniversariantesDaSemana() {
        return repository.findAniversariantesDaSemana();
    }

    public List<Membro> getAniversariantesDoMes() {
        return repository.findAniversariantesDoMes();
    }
    // MembroService
    public Page<Membro> getMembrosInativos(int mesesInatividade, Pageable pageable) {
        LocalDate dataLimite = LocalDate.now().minusMonths(mesesInatividade);
        return repository.findInativosPorMeses(dataLimite, pageable);
    }

    public boolean isAptoParaVotacao(Membro m) {
        if (m == null) {
            return false;
        }

        LocalDate hoje = LocalDate.now();

        // 1. Membro deve estar ativo
        if (Boolean.FALSE.equals(m.getAtivo()) || Boolean.TRUE.equals(m.getArquivado())) {
            return false;
        }

        // 2. Deve ter sido batizado
        if (m.getDataBatismo() == null) {
            return false;
        }

        // 3. Idade mínima: 16 anos (ajuste conforme regra da sua igreja)
        LocalDate dataNascimento = m.getDataNascimento();
        if (dataNascimento == null || Period.between(dataNascimento, hoje).getYears() < 16) {
            return false;
        }

        // 4. Tempo mínimo desde o batismo: pelo menos 6 meses
        if (Period.between(m.getDataBatismo(), hoje).toTotalMonths() < 6) {
            return false;
        }

        // 5. Não pode estar sob disciplina
        if (Boolean.TRUE.equals(m.getSobDisciplina())) {
            return false;
        }

        // 6. Frequência: não pode ter muitas faltas consecutivas (ex: mais de 3)

        // 7. Presença recente: última presença não pode ser muito antiga (ex: mais de 3 meses)
        if (m.getDataUltimaPresenca() != null) {
            if (Period.between(m.getDataUltimaPresenca(), hoje).toTotalMonths() > 3) {
                return false;
            }
        } else {
            // Se nunca registrou presença, considera não apto
            return false;
        }

        // Se passou em todos os critérios
        return true;
    }
}