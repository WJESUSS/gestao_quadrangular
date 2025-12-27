package com.igreja.GestaoQuadrangular.servicce; // ← Já corrigido: servicce → service

import com.igreja.GestaoQuadrangular.application.dto.*;
import com.igreja.GestaoQuadrangular.domain.entity.*;
import com.igreja.GestaoQuadrangular.domain.repository.*;
import com.igreja.GestaoQuadrangular.num.EscadaSucesso;
import com.igreja.GestaoQuadrangular.num.Role;
import com.igreja.GestaoQuadrangular.web.exception.ResourceNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.igreja.GestaoQuadrangular.num.Role.ROLE_MEMBRO;

@Service
public class CelulaService {

    private final RelatorioRepository relatorioRepository;
    private final UsuarioRepository usuarioRepository;
    private final CelulaRepository celulaRepository;
    private final LiderRepository liderRepository;
    private final MembroRepository membroRepository;
    private final RelatorioSemanalRepository relatorioSemanalRepository;
    private final MetaCelulaRepository metaCelulaRepository;
    private final MensagemChatRepository mensagemChatRepository;

    public CelulaService(RelatorioRepository relatorioRepository, PresencaRepository presencaRepository, UsuarioRepository usuarioRepository, CelulaRepository celulaRepository,
                         LiderRepository liderRepository,
                         MembroRepository membroRepository,
                         RelatorioSemanalRepository relatorioSemanalRepository,
                         MetaCelulaRepository metaCelulaRepository,
                         MensagemChatRepository mensagemChatRepository) {
        this.relatorioRepository = relatorioRepository;
        this.usuarioRepository = usuarioRepository;
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
        // 1. Se for ADMIN ou PASTOR, permite sempre (Ignora a trava de ID)
        if (usuarioLogado.getRole() == Role.ROLE_ADMIN || usuarioLogado.getRole() == Role.ROLE_PASTOR) {
            return;
        }

        // 2. Se for Líder, verifica se ele lidera ESTA célula específica
        if (usuarioLogado.getRole() == Role.ROLE_LIDER) {
            Lider lider = liderRepository.findByUsuarioId(usuarioLogado.getId())
                    .orElseThrow(() -> new RuntimeException("Líder não encontrado para este usuário"));

            // Compara o ID do líder da célula com o ID do líder logado
            if (celula.getLider() == null || !celula.getLider().getId().equals(lider.getId())) {
                throw new RuntimeException("Ação permitida apenas para o líder desta célula ou administradores");
            }
            return;
        }

        // 3. Se não for nenhum dos acima, bloqueia
        throw new RuntimeException("Você não tem permissão para realizar esta ação");
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
    public List<Usuario> listarUsuariosNaoNaCelula() {
        // Role deve começar com R maiúsculo para referenciar o Enum
        return usuarioRepository.findByCelulaIsNullAndRole(Role.ROLE_MEMBRO);
    }
    @Transactional
    public void adicionarMembroACelula(Long membroId, Long celulaId, Usuario usuarioLogado) {

        Usuario membro = usuarioRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro não encontrado com ID: " + membroId));

        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada com ID: " + celulaId));

        if (membro.getCelula() != null) {
            throw new RuntimeException(
                    "Membro já pertence à célula: " + membro.getCelula().getNome()
            );
        }

        // opcional: validar líder
        if (usuarioLogado.getRole() == Role.ROLE_LIDER) {
            validarPermissaoLiderDaCelula(celula, usuarioLogado);
        }

        membro.setCelula(celula);
        celula.getMembros().add(membro);

        usuarioRepository.save(membro);
        celulaRepository.save(celula);
    }

    @Transactional(readOnly = true)
    public List<CelulaDTO> listarTodasDTO() {
        List<Celula> celulas = celulaRepository.findAll();
        celulas.forEach(c -> Hibernate.initialize(c.getMembros())); // garante carregamento de membros

        return celulas.stream()
                .map(c -> new CelulaDTO(
                        c.getId(),
                        c.getNome(),
                        c.getEndereco(),
                        c.getDiaSemana(),
                        c.getHorario(),
                        c.isCasaDePaz(),
                        c.getLider() != null ? new CelulaDTO.LiderDTO(
                                c.getLider().getId(),
                                c.getLider().getNome(),
                                c.getLider().getEmail()
                        ) : null,
                        c.getMembros().stream()
                                .map(m -> new CelulaDTO.MembroDTO(
                                        m.getId(),
                                        m.getNome(),
                                        m.getEmail()
                                ))
                                .toList(),
                        c.getDataCriacao()
                ))
                .toList();
    }
    public Map<String, Object> gerarDashboardCompleto() {
        Map<String, Object> stats = new HashMap<>();

        // 1. Total de Membros Ativos
        long totalMembros = membroRepository.countByArquivadoFalse();

        // 2. Soma de Conversões e Batismos (dados que você acabou de enviar via Relatório)
        Integer conversoes = relatorioSemanalRepository.somarTodasConversoes();
        Integer batismos = relatorioSemanalRepository.somarTodosBatismos();
        Integer visitantes = relatorioSemanalRepository.somarTodosVisitantes();

        // 3. Monta o Map exatamente como o Frontend PastorAdmin.jsx espera
        stats.put("totalMembros", totalMembros);
        stats.put("conversoesMes", conversoes != null ? conversoes : 0);
        stats.put("batismosMes", batismos != null ? batismos : 0);
        stats.put("visitantesSemana", visitantes != null ? visitantes : 0);
        stats.put("frequenciaGeral", 85); // Valor exemplo, pode calcular a média se desejar

        return stats;
    }


    @Transactional(readOnly = true)
    public CelulaDTO buscarPorIdDTO(Long celulaId) {
        // Busca a entidade
        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));

        // Mapeamento manual para quebrar a recursão infinita do JSON
        CelulaDTO dto = new CelulaDTO();
        dto.setId(celula.getId());
        dto.setNome(celula.getNome());
        dto.setEndereco(celula.getEndereco());
        dto.setDiaSemana(celula.getDiaSemana());
        dto.setHorario(celula.getHorario());
        dto.setCasaDePaz(celula.isCasaDePaz());

        // Mapeia o Líder (usando apenas dados simples)
        if (celula.getLider() != null) {
            dto.setLider(new CelulaDTO.LiderDTO(
                    celula.getLider().getId(),
                    celula.getLider().getNome(),
                    celula.getLider().getEmail()
            ));
        }

        // Mapeia os Membros (transformando a lista de entidades em lista de DTOs)
        if (celula.getMembros() != null) {
            dto.setMembros(celula.getMembros().stream()
                    .map(m -> new CelulaDTO.MembroDTO(m.getId(), m.getNome(), m.getEmail()))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
    // Dentro do seu CelulaService


    public Optional<RelatorioPorDataDTO> buscarRelatorioPorData(Long celulaId, LocalDate data) {
        // 1. Busca a célula para ter os dados base (Líder e Total de Membros)
        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));

        // 2. Tenta encontrar o relatório físico no banco
        return relatorioRepository.findByCelulaIdAndData(celulaId, data)
                .map(relatorio -> {
                    int totalMembros = celula.getMembros().size();
                    int presentes = relatorio.getMembrosPresentes().size();

                    return RelatorioPorDataDTO.builder()
                            .celulaId(celula.getId())
                            .nomeCelula(celula.getNome())
                            .nomeLider(celula.getLider() != null ? celula.getLider().getNome() : "Sem Líder")
                            .data(relatorio.getData())
                            .statusReuniao("REALIZADA")
                            .relatorioEnviado(true)
                            .totalMembrosNaCelula(totalMembros)
                            .presentes(presentes)
                            .faltosos(totalMembros - presentes)
                            .percentualPresenca(totalMembros > 0 ? (presentes * 100.0) / totalMembros : 0)
                            .conversoes(relatorio.getConversoes())
                            .novasVisitantes(relatorio.getNovasVisitantes())
                            .observacoesLider(relatorio.getObservacoes())
                            .build();
                });
    }
    public List<CelulaStatusDTO> listarStatusRelatorios(LocalDate inicio, LocalDate fim) {
        List<Celula> celulas = celulaRepository.findAll();

        return celulas.stream().map(celula -> {
            String nomeLider = (celula.getLider() != null)
                    ? celula.getLider().getNome()
                    : "Sem Líder";

            boolean entregou = relatorioSemanalRepository
                    .existeRelatorioNoPeriodo(celula.getId(), inicio, fim);

            String statusFinal = entregou ? "ENTREGUE" : "PENDENTE";

            return new CelulaStatusDTO(
                    celula.getId(),
                    celula.getNome(),
                    celula.getEndereco(),
                    celula.getDiaSemana(),
                    celula.getHorario(),
                    celula.isCasaDePaz(),
                    nomeLider,
                    statusFinal,
                    celula.getMembros() != null ? celula.getMembros().size() : 0
            );
        }).collect(Collectors.toList());
    }
    @Transactional
    public void atualizarEtapaMembro(AtualizarEtapaDTO dto) {
        // 1. Busca o membro
        Membro membro = membroRepository.findById(dto.membroId())
                .orElseThrow(() -> new RuntimeException("Membro não encontrado com ID: " + dto.membroId()));

        // 2. Converte a String para o Enum EscadaSucesso
        // O valueOf espera o nome exato (ex: "GANHAR", "CONSOLIDAR")
        try {
            EscadaSucesso novaEtapa = EscadaSucesso.valueOf(dto.etapaAtual().toUpperCase());
            membro.setEscadaSucesso(novaEtapa);

            // Se você tiver esse campo de data no banco, é bom atualizar
            membro.setDataUltimaAtualizacaoEscada(LocalDate.now());

            membroRepository.save(membro);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("A etapa '" + dto.etapaAtual() + "' não é válida no sistema.");
        }
    }

}