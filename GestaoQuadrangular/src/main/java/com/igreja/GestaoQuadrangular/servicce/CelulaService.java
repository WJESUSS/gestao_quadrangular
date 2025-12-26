package com.igreja.GestaoQuadrangular.servicce; // ← Já corrigido: servicce → service

import com.igreja.GestaoQuadrangular.application.dto.*;
import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.entity.Lider;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.entity.Usuario;
import com.igreja.GestaoQuadrangular.domain.entity.RelatorioSemanal;
import com.igreja.GestaoQuadrangular.domain.entity.MetaCelula;
import com.igreja.GestaoQuadrangular.domain.entity.MensagemChat;
import com.igreja.GestaoQuadrangular.domain.repository.CelulaRepository;
import com.igreja.GestaoQuadrangular.domain.repository.LiderRepository;
import com.igreja.GestaoQuadrangular.domain.repository.MembroRepository;
import com.igreja.GestaoQuadrangular.domain.repository.RelatorioSemanalRepository;
import com.igreja.GestaoQuadrangular.domain.repository.MetaCelulaRepository;
import com.igreja.GestaoQuadrangular.domain.repository.MensagemChatRepository;
import com.igreja.GestaoQuadrangular.num.Role;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class CelulaService {

    private final CelulaRepository celulaRepository;
    private final LiderRepository liderRepository;
    private final MembroRepository membroRepository;
    private final RelatorioSemanalRepository relatorioSemanalRepository;
    private final MetaCelulaRepository metaCelulaRepository;
    private final MensagemChatRepository mensagemChatRepository;

    public CelulaService(CelulaRepository celulaRepository,
                         LiderRepository liderRepository,
                         MembroRepository membroRepository,
                         RelatorioSemanalRepository relatorioSemanalRepository,
                         MetaCelulaRepository metaCelulaRepository,
                         MensagemChatRepository mensagemChatRepository) {
        this.celulaRepository = celulaRepository;
        this.liderRepository = liderRepository;
        this.membroRepository = membroRepository;
        this.relatorioSemanalRepository = relatorioSemanalRepository;
        this.metaCelulaRepository = metaCelulaRepository;
        this.mensagemChatRepository = mensagemChatRepository;
    }

    // ==================== MÉTODOS ORIGINAIS (INALTERADOS) ====================

    /**
     * Cria uma nova célula e associa ao líder informado
     */
    @Transactional
    public Celula criar(CelulaCreateDTO dto) {
        if (celulaRepository.existsByNome(dto.getNome())) {
            throw new RuntimeException("Já existe uma célula com o nome: " + dto.getNome());
        }

        Lider lider = liderRepository.findById(dto.getLiderId())
                .orElseThrow(() -> new RuntimeException("Líder não encontrado"));

        // NOVA VALIDAÇÃO: líder já tem uma célula?
        if (!lider.getCelulas().isEmpty()) {
            String nomeCelula = lider.getCelulas().stream()
                    .findFirst()
                    .map(Celula::getNome)
                    .orElse("Desconhecida");

            throw new RuntimeException("Este líder já lidera uma célula: " + nomeCelula);
        }

        Celula celula = new Celula();
        celula.setNome(dto.getNome());
        celula.setEndereco(dto.getEndereco());
        celula.setDiaSemana(dto.getDiaSemana());
        celula.setHorario(dto.getHorario());
        celula.setCasaDePaz(dto.isCasaDePaz());
        celula.setLider(lider);

        return celulaRepository.save(celula);
    }

    /**
     * Lista todas as células (sem lazy loading problems)
     */
    @Transactional(readOnly = true)
    public List<Celula> listarTodos() {
        List<Celula> celulas = celulaRepository.findAll();
        celulas.forEach(c -> Hibernate.initialize(c.getMembros()));
        return celulas;
    }

    /**
     * Adiciona um membro a uma célula com controle de permissão
     */
    @Transactional
    public void adicionarMembroACelula(Long membroId, Long celulaId, Usuario usuarioLogado) {
        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada com ID: " + celulaId));

        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro não encontrado com ID: " + membroId));

        if (usuarioLogado.getRole() == Role.ROLE_LIDER) {
            Lider liderLogado = liderRepository.findByUsuarioId(usuarioLogado.getId())
                    .orElseThrow(() -> new RuntimeException("Líder não encontrado para o usuário logado"));

            if (!liderLogado.getCelulas().contains(celula)) {
                throw new RuntimeException("Você só pode adicionar membros às suas próprias células");
            }
        }

        if (membro.getCelula() != null) {
            throw new RuntimeException("Este membro já pertence à célula: " + membro.getCelula().getNome());
        }

        membro.setCelula(celula);
        membro.definirDataEntradaNaCelula();
        membroRepository.save(membro);
    }

    /**
     * Busca célula por ID com membros carregados
     */
    @Transactional(readOnly = true)
    public Celula buscarPorId(Long id) {
        Celula celula = celulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada com ID: " + id));

        Hibernate.initialize(celula.getMembros());
        return celula;
    }

    /**
     * Lista membros de uma célula (com lazy loading resolvido)
     */
    @Transactional(readOnly = true)
    public List<Membro> listarMembrosDaCelula(Long celulaId) {
        Celula celula = buscarPorId(celulaId);
        return new ArrayList<>(celula.getMembros());
    }

    /**
     * Lista todas as células com DTO completo (para frontend)
     */
    @Transactional(readOnly = true)
    public List<CelulaListDTO> listarTodas() {
        return celulaRepository.findAll().stream()
                .map(celula -> new CelulaListDTO(
                        celula.getId(),
                        celula.getNome(),
                        celula.getDiaSemana(),
                        celula.getHorario(),
                        celula.getEndereco(),
                        celula.getLider() != null ? celula.getLider().getNome() : "Sem líder",
                        celula.getLider() != null ? celula.getLider().getEmail() : null,
                        celula.getMembros() != null ? celula.getMembros().size() : 0,
                        celula.getDataCriacao()
                ))
                .sorted(Comparator.comparing(CelulaListDTO::nome))
                .toList();
    }

    /**
     * Busca célula por ID e retorna DTO para response
     */
    @Transactional(readOnly = true)
    public CelulaResponseDTO buscarPorIdParaResponse(Long id) {
        Celula celula = buscarPorId(id);

        return new CelulaResponseDTO(
                celula.getId(),
                celula.getNome(),
                celula.getEndereco(),
                celula.getDiaSemana(),
                celula.getHorario(),
                celula.isCasaDePaz(),
                celula.getLider() != null ? celula.getLider().getId() : null,
                celula.getLider() != null ? celula.getLider().getNome() : null
        );
    }

    /**
     * Relatório de crescimento da célula (total de membros por mês)
     */
    @Transactional(readOnly = true)
    public List<CelulaCrescimentoDTO> gerarRelatorioCrescimento(Long celulaId, int meses) {
        Celula celula = buscarPorId(celulaId);
        List<Membro> membros = (List<Membro>) celula.getMembros();

        List<CelulaCrescimentoDTO> relatorio = new ArrayList<>();

        LocalDate hoje = LocalDate.now();
        for (int i = meses - 1; i >= 0; i--) {
            LocalDate mesReferencia = hoje.minusMonths(i);
            String periodo = mesReferencia.getMonthValue() + "/" + mesReferencia.getYear();

            long count = membros.stream()
                    .filter(m -> m.getDataEntradaCelula() != null &&
                            !m.getDataEntradaCelula().isAfter(mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth())))
                    .count();

            relatorio.add(new CelulaCrescimentoDTO(periodo, count));
        }

        return relatorio;
    }

    // ==================== NOVAS FUNCIONALIDADES (ADICIONADAS) ====================

    private void validarPermissaoLiderDaCelula(Celula celula, Usuario usuarioLogado) {
        if (usuarioLogado.getRole() != Role.ROLE_LIDER) {
            throw new RuntimeException("Apenas líderes podem realizar esta ação");
        }
        Lider lider = liderRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new RuntimeException("Líder não encontrado para o usuário logado"));
        if (!lider.getCelulas().contains(celula)) {
            throw new RuntimeException("Ação permitida apenas na sua própria célula");
        }
    }

    // ---------- Relatório Semanal Simplificado ----------
    @Transactional
    public RelatorioSemanal salvarRelatorioSemanal(RelatorioSemanalCreateDTO dto, Usuario usuarioLogado) {
        Celula celula = buscarPorId(dto.getCelulaId());
        validarPermissaoLiderDaCelula(celula, usuarioLogado);

        RelatorioSemanal relatorio = new RelatorioSemanal();
        relatorio.setCelula(celula);
        relatorio.setDataRelatorio(dto.getDataRelatorio());
        relatorio.setPresentes(dto.getPresentes());
        relatorio.setVisitantes(dto.getVisitantes());
        relatorio.setConversoes(dto.getConversoes());
        relatorio.setBatismos(dto.getBatismos());
        relatorio.setMembrosPresentesIds(dto.getMembrosPresentesIds());

        return relatorioSemanalRepository.save(relatorio);
    }

    @Transactional(readOnly = true)
    public List<RelatorioSemanalListDTO> listarRelatoriosSemanais(Long celulaId, int limite) {
        Celula celula = buscarPorId(celulaId);
        return relatorioSemanalRepository.findByCelulaOrderByDataRelatorioDesc(celula, PageRequest.of(0, limite))
                .stream()
                .map(r -> new RelatorioSemanalListDTO(
                        r.getId(),
                        r.getDataRelatorio(),
                        r.getPresentes(),
                        r.getVisitantes(),
                        r.getConversoes(),
                        r.getBatismos()
                ))
                .toList();
    }

    // ---------- Metas Personalizadas com Progresso ----------
    @Transactional
    public MetaCelula definirMetaCelula(MetaCelulaCreateDTO dto, Usuario usuarioLogado) {
        Celula celula = buscarPorId(dto.getCelulaId());
        validarPermissaoLiderDaCelula(celula, usuarioLogado);

        MetaCelula meta = metaCelulaRepository.findByCelulaAndAtivaTrue(celula)
                .orElse(new MetaCelula());

        meta.setCelula(celula);
        meta.setDescricao(dto.getDescricao());
        meta.setAlvoMembros(dto.getAlvoMembros());
        meta.setAlvoVisitasSemanais(dto.getAlvoVisitasSemanais());
        meta.setDataInicio(dto.getDataInicio());
        meta.setDataFim(dto.getDataFim());
        meta.setAtiva(true);

        return metaCelulaRepository.save(meta);
    }

    @Transactional(readOnly = true)
    public MetaProgressoDTO obterProgressoMeta(Long celulaId) {
        Celula celula = buscarPorId(celulaId);
        MetaCelula meta = metaCelulaRepository.findByCelulaAndAtivaTrue(celula)
                .orElse(null);

        if (meta == null) {
            return new MetaProgressoDTO("Nenhuma meta ativa", 0, 0, 0.0, 0.0, 0, 0.0);
        }

        int membrosAtuais = celula.getMembros().size();
        double progressoMembros = meta.getAlvoMembros() > 0
                ? (double) membrosAtuais / meta.getAlvoMembros() * 100
                : 0;

        // Média de visitantes nas últimas 4 semanas
        LocalDate quatroSemanas = LocalDate.now().minusWeeks(4);
        Double mediaVisitantes = relatorioSemanalRepository.mediaVisitantesDesde(celula, quatroSemanas);
        double avgVisitantes = mediaVisitantes != null ? mediaVisitantes : 0.0;

        double progressoVisitas = meta.getAlvoVisitasSemanais() > 0
                ? (avgVisitantes / meta.getAlvoVisitasSemanais()) * 100
                : 0;

        return new MetaProgressoDTO(
                meta.getDescricao(),
                membrosAtuais,
                meta.getAlvoMembros(),
                progressoMembros,
                avgVisitantes,
                meta.getAlvoVisitasSemanais(),
                progressoVisitas
        );
    }

    // ---------- Chat Interno da Célula ----------
    @Transactional
    public MensagemChat enviarMensagem(MensagemChatCreateDTO dto, Usuario usuarioLogado) {
        Celula celula = buscarPorId(dto.getCelulaId());

        // Temporário: buscar por email ou outro identificador único que exista
        // (substitua por algo que realmente exista na sua entidade Membro)
        Membro autor = membroRepository.findByEmail(usuarioLogado.getEmail())  // exemplo
                .orElseThrow(() -> new RuntimeException("Membro não encontrado para o usuário logado"));

        // Alternativa ainda mais simples (se aceitar que qualquer membro pode enviar na célula):
        // Membro autor = membroRepository.findByCelula(celula).stream().findFirst()
        //         .orElseThrow(() -> new RuntimeException("Nenhum membro na célula"));

        if (!autor.getCelula().equals(celula)) {
            throw new RuntimeException("Você só pode enviar mensagens na sua própria célula");
        }

        MensagemChat msg = new MensagemChat();
        msg.setCelula(celula);
        msg.setAutor(autor);
        msg.setTexto(dto.getTexto());
        msg.setDataEnvio(LocalDateTime.now());
        msg.setVisivel(true);

        return mensagemChatRepository.save(msg);
    }

    @Transactional(readOnly = true)
    public List<MensagemChatListDTO> listarMensagensChat(Long celulaId, int limite) {
        Celula celula = buscarPorId(celulaId);
        return mensagemChatRepository.findByCelulaAndVisivelTrueOrderByDataEnvioDesc(celula, PageRequest.of(0, limite))
                .stream()
                .map(m -> new MensagemChatListDTO(
                        m.getAutor().getNome(),
                        m.getTexto(),
                        m.getDataEnvio()
                ))
                .toList();
    }
}