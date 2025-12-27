package com.igreja.GestaoQuadrangular.servicce;  // ← CORRIGIDO: servicce → service

import com.igreja.GestaoQuadrangular.application.dto.*;
import com.igreja.GestaoQuadrangular.domain.entity.*;
import com.igreja.GestaoQuadrangular.domain.repository.*;
import com.igreja.GestaoQuadrangular.num.Role;
import com.igreja.GestaoQuadrangular.num.StatusEspiritual;
import com.igreja.GestaoQuadrangular.infrastructure.mail.EmailJaCadastradoException;
import com.igreja.GestaoQuadrangular.web.exception.ResourceNotFoundException;
import com.igreja.GestaoQuadrangular.web.exception.UsuarioJaVinculadoALiderException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PastorService {


    private final RelatorioRepository relatorioRepository;
    private final VisitanteRepository visitanteRepository;
    private final RelatorioSemanalRepository relatorioSemanalRepository;
    private final PastorRepository pastorRepository;
    private final UsuarioRepository usuarioRepository;
    private final MembroRepository membroRepository;
    private final CelulaRepository celulaRepository;
    private final LiderRepository liderRepository;
    private final PasswordEncoder passwordEncoder;

    public PastorService(
            RelatorioRepository relatorioRepository, VisitanteRepository visitanteRepository, RelatorioSemanalRepository relatorioSemanalRepository, PastorRepository pastorRepository, UsuarioRepository usuarioRepository,
            MembroRepository membroRepository,
            CelulaRepository celulaRepository,
            LiderRepository liderRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.relatorioRepository = relatorioRepository;
        this.visitanteRepository = visitanteRepository;
        this.relatorioSemanalRepository = relatorioSemanalRepository;
        this.pastorRepository = pastorRepository;
        this.usuarioRepository = usuarioRepository;
        this.membroRepository = membroRepository;
        this.celulaRepository = celulaRepository;
        this.liderRepository = liderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // CRIAR PASTOR
    // =========================
    @Transactional
    public Pastor criarPastorCompleto(CriarPastorDTO dto) {
        // 1. Verifica se e-mail já existe
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        // 2. Cria o usuário com role PASTOR
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setTelefone(dto.telefone());
        usuario.setRole(Role.ROLE_PASTOR);
        usuario.setAtivo(true);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // 3. Cria o registro na tabela pastores (OBRIGATÓRIO!)
        Pastor pastor = new Pastor();
        pastor.setUsuario(usuarioSalvo);
        pastor.setDataOrdenacao(dto.dataOrdenacao());      // ← adicione esse campo no DTO
        pastor.setIgrejaOrdenacao(dto.igrejaOrdenacao());  // ← adicione esse campo no DTO
        pastor.setPastorPrincipal(dto.pastorPrincipal());  // ← opcional, default false

        return pastorRepository.save(pastor);
    }

    // =========================
    // DASHBOARD DO PASTOR
    // =========================
    public PastorDashboardDTO dashboard() {
        long totalMembros = membroRepository.count();
        long membrosArquivados = membroRepository.countByArquivadoTrue();
        long totalCelulas = celulaRepository.count();
        long totalLideres = liderRepository.count();

        return new PastorDashboardDTO(
                totalMembros,
                totalCelulas,
                membrosArquivados,
                totalLideres
        );
    }

    // =========================
    // CRUD LÍDER (SÓ PASTOR)
    // =========================
    public Lider criarLider(CriarLiderDTO dto) {
        Usuario pastorLogado = getPastorLogado();

        if (pastorLogado.getRole() != Role.ROLE_PASTOR) {
            throw new SecurityException("Apenas pastores podem criar líderes");
        }

        if (liderRepository.existsByEmail(dto.email())) {
            throw new EmailJaCadastradoException("E-mail já cadastrado para outro líder");
        }

        if (liderRepository.existsByUsuarioId(dto.usuarioId())) {
            throw new UsuarioJaVinculadoALiderException("Este usuário já está vinculado a um líder");
        }

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // NÃO ALTERE A ROLE DO USUÁRIO AQUI!
        // Ele pode continuar sendo MEMBRO ou PASTOR

        Lider lider = new Lider();
        lider.setNome(dto.nome());
        lider.setEmail(dto.email());
        lider.setTelefone(dto.telefone());
        lider.setUsuario(usuario);

        return liderRepository.save(lider);
    }

    public List<Lider> listarTodosLideres() {
        return liderRepository.findAll();
    }

    public Lider buscarLiderPorId(Long id) {
        return liderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Líder não encontrado com id: " + id));
    }

    public Lider atualizarLider(Long id, Lider lider) {
        Lider existente = buscarLiderPorId(id);
        existente.setNome(lider.getNome());
        existente.setEmail(lider.getEmail());
        existente.setTelefone(lider.getTelefone());
        return liderRepository.save(existente);
    }

    public void deletarLider(Long id) {
        Lider lider = liderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Líder não encontrado com id: " + id));
        liderRepository.delete(lider);
    }

    private Usuario getPastorLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == "anonymousUser") {
            throw new RuntimeException("Usuário não autenticado");
        }

        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Pastor logado não encontrado"));
    }

    public Usuario criarUsuarioComum(CriarUsuarioDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setTelefone(dto.telefone());
        usuario.setRole(Role.ROLE_MEMBRO);
        usuario.setAtivo(true);

        return usuarioRepository.save(usuario);
    }

    public Celula adicionarMembroACelulaDoLider(Long membroId, Usuario liderLogado) {
        Lider lider = liderRepository.findByUsuarioId(liderLogado.getId())
                .orElseThrow(() -> new RuntimeException("Líder não encontrado para este usuário"));

        Celula celula = lider.getCelulas().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Este líder não tem nenhuma célula associada"));

        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro não encontrado"));

        if (membro.getCelula() != null) {
            throw new RuntimeException("Este membro já está em outra célula");
        }

        membro.setCelula(celula);
        membroRepository.save(membro);

        return celula;
    }

    // =========================
    // DASHBOARD COMPLETO
    // =========================
    // =========================
// DASHBOARD COMPLETO (REAL)
// =========================
    public Map<String, Object> gerarDashboardCompleto() {
        Map<String, Object> data = new HashMap<>();

        // 1. Estatísticas de Membros (Real)
        long totalMembros = membroRepository.countByArquivadoFalse();
        data.put("totalMembros", totalMembros);

        // 2. Status dos membros (Real)
        Map<String, Integer> statusMembros = new HashMap<>();
        statusMembros.put("VERDE", (int) membroRepository.countByStatus(StatusEspiritual.VERDE));
        statusMembros.put("AMARELO", (int) membroRepository.countByStatus(StatusEspiritual.AMARELO));
        statusMembros.put("VERMELHO", (int) membroRepository.countByStatus(StatusEspiritual.VERMELHO));
        data.put("statusMembros", statusMembros);

        // 3. Dados Consolidados dos Relatórios das Células (REAL!)
        // Aqui somamos o que todos os líderes digitaram nos relatórios semanais
        Integer totalConversoes = relatorioSemanalRepository.somarTodasConversoes();
        Integer totalBatismos = relatorioSemanalRepository.somarTodosBatismos();
        long totalRelatoriosEnviados = relatorioSemanalRepository.count();

        data.put("conversoesMes", totalConversoes != null ? totalConversoes : 0);
        data.put("batismos", totalBatismos != null ? totalBatismos : 0);
        data.put("relatoriosSemana", totalRelatoriosEnviados);

        // 4. Visitantes Cadastrados (Onde o Luiz aparece!)
        long totalVisitantes = visitanteRepository.count();
        data.put("novosMembros", totalVisitantes); // No Dashboard do Pastor, os visitantes entram como potencial de novos membros

        // 5. Ranking e Frequência
        data.put("rankingCelulas", calcularRankingCelulas());

        // Simulação de presença baseada nos relatórios (Presentes / Total Membros)
        Integer somaPresentes = relatorioSemanalRepository.somarTodosPresentes();
        double freqGeral = totalMembros > 0 ? (somaPresentes.doubleValue() / totalMembros) * 100 : 0;
        data.put("frequenciaGeral", Math.round(freqGeral));

        return data;

    }


    // Método auxiliar para ranking de células
    private List<Map<String, Object>> calcularRankingCelulas() {
        Pageable top5 = PageRequest.of(0, 5);
        List<Object[]> resultados = celulaRepository.findTopCelulasPorTamanhoRaw(top5);

        List<Map<String, Object>> ranking = new ArrayList<>();

        int posicao = 1;
        for (Object[] linha : resultados) {
            String nome = (String) linha[0];

            // Conversão segura para evitar ClassCastException anterior
            Long tamanho = ((Number) linha[1]).longValue();

            String nomeLider = linha.length > 2 && linha[2] != null ? (String) linha[2] : "Sem líder";

            // CORREÇÃO PRINCIPAL: criar o mapa ANTES de usar
            Map<String, Object> item = new HashMap<>();

            item.put("posicao", posicao++);
            item.put("nome", nome);
            item.put("totalMembros", tamanho.intValue());
            item.put("pontos", tamanho.intValue() * 10);
            item.put("lider", nomeLider);  // agora usa o valor real da query

            ranking.add(item);
        }

        return ranking;
    }

    private List<Map<String, Object>> formatarCrescimentoMensal(List<Object[]> rawData, String label) {
        List<Map<String, Object>> resultado = new ArrayList<>();

        // Preencher os últimos 12 meses (ou todos os disponíveis)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -11); // começar 11 meses atrás

        Map<String, Long> mapaMeses = new LinkedHashMap<>();

        // Inicializar com zero os últimos 12 meses
        for (int i = 0; i < 12; i++) {
            String chave = (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
            mapaMeses.put(chave, 0L);
            cal.add(Calendar.MONTH, 1);
        }

        // Preencher com dados reais
        for (Object[] linha : rawData) {
            Integer mes = (Integer) linha[0];
            Integer ano = (Integer) linha[1];
            Long total = ((Number) linha[2]).longValue();

            String chave = mes + "/" + ano;
            mapaMeses.put(chave, total);
        }

        // Montar lista ordenada para o frontend
        for (Map.Entry<String, Long> entry : mapaMeses.entrySet()) {
            Map<String, Object> ponto = new HashMap<>();
            ponto.put("periodo", entry.getKey());
            ponto.put(label, entry.getValue());
            resultado.add(ponto);
        }

        return resultado;
    }

    public Lider promoverMembroALider(PromoverLiderDTO dto) {
        Usuario pastorLogado = getPastorLogado();
        if (pastorLogado.getRole() != Role.ROLE_PASTOR) {
            throw new SecurityException("Apenas pastores podem promover líderes");
        }

        if (liderRepository.existsByUsuarioId(dto.usuarioId())) {
            throw new UsuarioJaVinculadoALiderException("Este usuário já é líder");
        }

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // Opcional: validar que o usuário tem role MEMBRO (ou permitir outros?)
        if (usuario.getRole() != Role.ROLE_MEMBRO) {
            throw new IllegalArgumentException("Apenas membros podem ser promovidos a líderes");
        }

        Lider lider = new Lider();
        lider.setNome(usuario.getNome());
        lider.setEmail(usuario.getEmail());
        lider.setTelefone(usuario.getTelefone());
        lider.setUsuario(usuario);

        return liderRepository.save(lider);
    }

    @Transactional
    public Pastor cadastrarPerfilPastor(Long usuarioId, LocalDate dataOrdenacao, String igrejaOrdenacao, boolean pastorPrincipal) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (usuario.getRole() != Role.ROLE_PASTOR) {
            throw new IllegalArgumentException("Apenas usuários com role PASTOR podem ter perfil de pastor");
        }

        // Se já existir, atualiza. Se não, cria novo.
        Pastor pastor = pastorRepository.findByUsuario(usuario).orElse(new Pastor());
        pastor.setUsuario(usuario);
        pastor.setDataOrdenacao(dataOrdenacao);
        pastor.setIgrejaOrdenacao(igrejaOrdenacao);
        pastor.setPastorPrincipal(pastorPrincipal);

        return pastorRepository.save(pastor);
    }

    public Usuario criarUsuarioPastor(@Valid CriarPastorDTO dto) {
        // Validação: e-mail não pode estar cadastrado
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("E-mail já cadastrado no sistema");
        }

        Usuario pastor = new Usuario();
        pastor.setNome(dto.nome());
        pastor.setEmail(dto.email());
        pastor.setSenha(passwordEncoder.encode(dto.senha())); // Senha criptografada
        pastor.setTelefone(dto.telefone());

        // Campos opcionais do DTO (ajuste conforme sua classe CriarPastorDTO)
        if (dto.sobrenome() != null) {
            pastor.setSobrenome(dto.sobrenome());
        }
        if (dto.titulo() != null) {
            pastor.setTitulo(dto.titulo()); // ex: "Pr.", "Pastora", etc.
        }

        // Configurações obrigatórias para pastor
        pastor.setRole(Role.ROLE_PASTOR);
        pastor.setAtivo(true);

        // Salva e retorna o usuário
        return usuarioRepository.save(pastor);
    }

    @Transactional
    public Pastor promoverUsuarioAPastor(
            @NotNull(message = "ID do usuário é obrigatório") Long usuarioId,
            LocalDate dataOrdenacao,
            @NotNull(message = "Igreja de ordenação é obrigatória") String igrejaOrdenacao,
            boolean pastorPrincipal) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        if (usuario.getRole() == Role.ROLE_PASTOR && pastorRepository.findByUsuario(usuario).isPresent()) {
            throw new IllegalArgumentException("Este usuário já é um pastor completo.");
        }

        // Promove a role
        usuario.setRole(Role.ROLE_PASTOR);
        usuarioRepository.save(usuario);

        // Cria perfil de pastor
        Pastor pastor = new Pastor();
        pastor.setUsuario(usuario);
        pastor.setDataOrdenacao(dataOrdenacao);
        pastor.setIgrejaOrdenacao(igrejaOrdenacao);
        pastor.setPastorPrincipal(pastorPrincipal);

        return pastorRepository.save(pastor);
    }

    @Transactional(readOnly = true)
    public List<Pastor> listarTodos() {
        return pastorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pastor buscarPorId(Long id) {
        return pastorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pastor com ID " + id + " não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<PastorResponseDTO> listarTodosDTO() {
        return pastorRepository.findAll().stream()
                .map(p -> new PastorResponseDTO(
                        p.getId(),
                        p.getUsuario().getId(),
                        p.getUsuario().getNome(),
                        p.getUsuario().getEmail(),
                        p.getUsuario().getTelefone(),
                        p.getDataOrdenacao(),
                        p.getIgrejaOrdenacao(),
                        p.isPastorPrincipal()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public PastorResponseDTO buscarPorIdDTO(Long id) {
        Pastor pastor = pastorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pastor com ID " + id + " não encontrado"));

        return new PastorResponseDTO(
                pastor.getId(),
                pastor.getUsuario().getId(),
                pastor.getUsuario().getNome(),
                pastor.getUsuario().getEmail(),
                pastor.getUsuario().getTelefone(),
                pastor.getDataOrdenacao(),
                pastor.getIgrejaOrdenacao(),
                pastor.isPastorPrincipal()
        );
    }

    @Transactional(readOnly = true)
    public List<CelulaStatusDTO> obterStatusCelulas(LocalDate inicio, LocalDate fim) {

        List<Celula> celulas = celulaRepository.findAll();

        return celulas.stream().map(celula -> {

            // 1. Verifica se o relatório foi enviado
            boolean entregue = relatorioRepository.existeRelatorioNoPeriodo(
                    celula.getId(), inicio, fim
            );

            String liderNome = celula.getLider() != null
                    ? celula.getLider().getNome()
                    : "Sem Líder";

            // 2. Conta membros da célula
            int membros = celula.getMembros() != null
                    ? celula.getMembros().size()
                    : 0;

            // 3. Conta visitantes **que realmente marcaram presença** via RelatorioSemanal
            Integer visitantesPresentes = relatorioSemanalRepository
                    .somarVisitantesPorCelulaNoPeriodo(celula.getId(), inicio, fim);

            if (visitantesPresentes == null) visitantesPresentes = 0;

            // 4. Total de presentes = membros + visitantes presentes
            int totalPresentes = membros + visitantesPresentes;


            return new CelulaStatusDTO(
                    celula.getId(),
                    celula.getNome(),
                    celula.getEndereco(),
                    celula.getDiaSemana(),
                    celula.getHorario(),
                    celula.isCasaDePaz(),
                    liderNome,
                    entregue ? "ENTREGUE" : "PENDENTE",
                    membros,
                    visitantesPresentes,
                    totalPresentes
            );

        }).collect(Collectors.toList());
    }


    @Transactional
    public void marcarPresencaVisitante(Long visitanteId, LocalDate data) {
        Visitante visitante = visitanteRepository.findById(visitanteId)
                .orElseThrow(() -> new RuntimeException("Visitante não encontrado"));

        visitante.setDataPrimeiraVisita(data);
        visitanteRepository.save(visitante);
    }


    @Transactional
    public void deletarRelatorioPorId(Long id) {
        if (!relatorioSemanalRepository.existsById(id)) {

            throw new ResourceNotFoundException("Relatório com ID " + id + " não encontrado");
        }
        relatorioSemanalRepository.deleteById(id);
    }

    // ==================== Outros métodos ====================


}