package com.igreja.GestaoQuadrangular.web.controller;



import com.igreja.GestaoQuadrangular.application.dto.*;
import com.igreja.GestaoQuadrangular.domain.entity.*;
import com.igreja.GestaoQuadrangular.domain.repository.CelulaRepository;
import com.igreja.GestaoQuadrangular.domain.repository.MembroRepository;
import com.igreja.GestaoQuadrangular.num.TipoReuniao;
import com.igreja.GestaoQuadrangular.servicce.*;
import com.igreja.GestaoQuadrangular.web.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pastor")
@PreAuthorize("hasRole('PASTOR') or hasRole('ADMIN')")
public class PastorController {

    private final TesourariaDashboardService tesourariaDashboardService;
    private final PresencaService presencaService;
    private final EmailService emailService;
    private final CelulaRepository celulaRepository;
    private final PastorService pastorService;
    private final MembroService membroService;
    private final CelulaService celulaService;
    private final MembroRepository membroRepository;

    public PastorController(
            TesourariaDashboardService tesourariaDashboardService,
            PresencaService presencaService,
            EmailService emailService,
            CelulaRepository celulaRepository,
            PastorService pastorService,
            MembroService membroService,
            CelulaService celulaService,
            MembroRepository membroRepository) {
        this.tesourariaDashboardService = tesourariaDashboardService;
        this.presencaService = presencaService;
        this.emailService = emailService;
        this.celulaRepository = celulaRepository;
        this.pastorService = pastorService;
        this.membroService = membroService;
        this.celulaService = celulaService;
        this.membroRepository = membroRepository;
    }

    // ========================== MEMBROS - RETORNA JSON RICO (IGUAL AO MembroController) ==========================

    @PostMapping("/membros")
    public ResponseEntity<Membro> criarMembro(@Valid @RequestBody Membro membro) {
        Membro criado = membroService.criar(membro);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/membros/{id}")
    @Transactional
    public ResponseEntity<Membro> atualizarMembro(
            @PathVariable Long id,
            @Valid @RequestBody MembroUpdateDTO dto) {

        Membro membro = membroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro com ID " + id + " não encontrado"));

        if (dto.nome() != null) membro.setNome(dto.nome());
        if (dto.telefone() != null) membro.setTelefone(dto.telefone());
        if (dto.email() != null) membro.setEmail(dto.email());
        if (dto.faltasConsecutivas() != null) membro.setFaltasConsecutivas(dto.faltasConsecutivas());
        if (dto.arquivado() != null) membro.setArquivado(dto.arquivado());

        if (dto.celulaId() != null) {
            Celula celula = celulaRepository.findById(dto.celulaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Célula com ID " + dto.celulaId() + " não encontrada"));
            membro.setCelula(celula);
        }

        Membro atualizado = membroService.salvar(membro);
        return ResponseEntity.ok(atualizado);
    }

    @PutMapping("/membros/{id}/faltas")
    public ResponseEntity<Membro> registrarFalta(@PathVariable Long id) {
        membroService.registrarFalta(id);
        return ResponseEntity.ok(membroService.buscarPorId(id));
    }

    @PutMapping("/membros/{id}/presenca")
    public ResponseEntity<Membro> registrarPresenca(@PathVariable Long id) {
        membroService.registrarPresenca(id);
        return ResponseEntity.ok(membroService.buscarPorId(id));
    }

    @PatchMapping("/membros/{id}/archive")
    public ResponseEntity<Membro> toggleArquivado(@PathVariable Long id) {
        membroService.toggleArquivado(id);
        return ResponseEntity.ok(membroService.buscarPorId(id));
    }

    @GetMapping("/membros")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Membro>> listarMembros() {
        List<Membro> membros = membroRepository.findAll();
        return ResponseEntity.ok(membros);
    }

    // ========================== DEMAIS ENDPOINTS (mantidos como estavam) ==========================

    @PostMapping("/celulas")
    public ResponseEntity<CelulaResponseDTO> criarCelula(@Valid @RequestBody CelulaCreateDTO dto) {
        Celula celula = celulaService.criar(dto);
        CelulaResponseDTO response = new CelulaResponseDTO(
                celula.getId(),
                celula.getNome(),
                celula.getEndereco(),
                celula.getDiaSemana(),
                celula.getHorario(),
                celula.isCasaDePaz(),
                celula.getLider().getId(),
                celula.getLider().getNome()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Object> dashboard() {
        return ResponseEntity.ok(pastorService.dashboard());
    }

    @GetMapping("/dashboard/completo")
    public ResponseEntity<Map<String, Object>> dashboardCompleto() {
        return ResponseEntity.ok(pastorService.gerarDashboardCompleto());
    }

    @PostMapping("/lider")
    public ResponseEntity<Lider> criarLider(@Valid @RequestBody CriarLiderDTO dto) {
        Lider lider = pastorService.criarLider(dto);
        return ResponseEntity.ok(lider);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> criarUsuario(@Valid @RequestBody CriarUsuarioDTO dto) {
        Usuario usuario = pastorService.criarUsuarioComum(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @GetMapping("/lideres")
    @Transactional(readOnly = true)
    public ResponseEntity<List<LiderResponseDTO>> listarLideres() {
        List<LiderResponseDTO> lideres = pastorService.listarTodosLideres().stream()
                .map(lider -> new LiderResponseDTO(
                        lider.getId(),
                        lider.getNome(),
                        lider.getTelefone(),
                        lider.getEmail(),
                        lider.getCelulas() != null ? lider.getCelulas().size() : 0
                ))
                .toList();
        return ResponseEntity.ok(lideres);
    }

    @PostMapping("/adicionar-membro-celula")
    @PreAuthorize("hasRole('PASTOR') or hasRole('LIDER')")
    public ResponseEntity<String> adicionarMembroACelula(
            @Valid @RequestBody AdicionarMembroCelulaDTO dto,
            Authentication authentication) {

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        celulaService.adicionarMembroACelula(dto.membroId(), dto.celulaId(), usuarioLogado);
        return ResponseEntity.ok("Membro adicionado à célula com sucesso!");
    }

    @GetMapping("/celulas")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CelulaListResponseDTO>> listarCelulas() {
        List<Celula> celulas = celulaService.listarTodos();
        List<CelulaListResponseDTO> response = celulas.stream()
                .map(c -> new CelulaListResponseDTO(
                        c.getId(),
                        c.getNome(),
                        c.getEndereco(),
                        c.getDiaSemana(),
                        c.getHorario(),
                        c.isCasaDePaz(),
                        c.getLider().getId(),
                        c.getLider().getNome(),
                        c.getMembros() != null ? c.getMembros().size() : 0
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/celulas/{celulaId}/membros")
    @Transactional(readOnly = true)
    public ResponseEntity<List<MembroNomeDTO>> listarMembrosDaCelula(@PathVariable Long celulaId) {
        List<Membro> membros = celulaService.listarMembrosDaCelula(celulaId);
        List<MembroNomeDTO> response = membros.stream()
                .filter(m -> !m.isArquivado())
                .map(m -> new MembroNomeDTO(m.getId(), m.getNome()))
                .sorted((a, b) -> a.nome().compareToIgnoreCase(b.nome()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/celulas/status")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CelulaStatusDTO>> listarCelulasComStatus() {
        List<Celula> celulas = celulaService.listarTodos();
        List<CelulaStatusDTO> response = celulas.stream()
                .map(celula -> new CelulaStatusDTO(
                        celula.getId(),
                        celula.getNome(),
                        celula.getEndereco(),
                        celula.getDiaSemana(),
                        celula.getHorario(),
                        celula.isCasaDePaz(),
                        celula.getLider() != null ? celula.getLider().getNome() : "Sem líder",
                        celula.isCasaDePaz() ? "Aberta (Casa de Paz)" : "Pendente",
                        celula.getMembros() != null ? celula.getMembros().size() : 0
                ))
                .sorted((a, b) -> {
                    if (a.casaDePaz() && !b.casaDePaz()) return -1;
                    if (!a.casaDePaz() && b.casaDePaz()) return 1;
                    return a.nome().compareToIgnoreCase(b.nome());
                })
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public String test() {
        return "PastorController está funcionando! 😊";
    }

    @GetMapping("/testar-email")
    public ResponseEntity<String> testarEmail() {
        emailService.enviarEmailTeste();
        return ResponseEntity.ok("E-mail de teste enviado! Verifique sua caixa de entrada (e spam).");
    }

    // ========================== PRESENÇA E RELATÓRIOS ==========================

    @PostMapping("/presenca/culto")
    public ResponseEntity<String> registrarPresencaCulto(@Valid @RequestBody RegistrarPresencaCultoDTO dto) {
        presencaService.registrarPresencaCulto(dto.tipoReuniao(), dto.data(), dto.presentesIds());
        return ResponseEntity.ok("Presença no culto registrada!");
    }

    @PostMapping("/presenca/discipulado")
    public ResponseEntity<String> registrarPresencaDiscipulado(@Valid @RequestBody RegistrarPresencaSimplesDTO dto) {
        presencaService.registrarPresencaDiscipulado(dto.data(), dto.presentesIds());
        return ResponseEntity.ok("Presença no discipulado registrada!");
    }

    @GetMapping("/presenca/listar")
    public ResponseEntity<List<PresencaResponseDTO>> listarPresencas(
            @RequestParam TipoReuniao tipoReuniao,
            @RequestParam LocalDate data) {
        List<PresencaResponseDTO> presencas = presencaService.listarPresencasPorTipoEData(tipoReuniao, data);
        return ResponseEntity.ok(presencas);
    }

    @GetMapping("/relatorio/faltas-domingo")
    public ResponseEntity<List<MembroFaltasDomingoDTO>> relatorioFaltasDomingo() {
        List<MembroFaltasDomingoDTO> relatorio = membroService.gerarRelatorioFaltasDomingoMesAtual();
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/dashboard/financia")
    @PreAuthorize("hasRole('PASTOR') or hasRole('TESOUREIRO')")
    public ResponseEntity<DashboardFinanceiroDTO> dashboardFinanceiro(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        LocalDate dataInicio = (inicio != null) ? inicio : YearMonth.now().atDay(1);
        LocalDate dataFim = (fim != null) ? fim : YearMonth.now().atEndOfMonth();

        if (dataInicio.isAfter(dataFim)) {
            return ResponseEntity.badRequest().body(null);
        }

        DashboardFinanceiroDTO dashboard = tesourariaDashboardService.getDashboard(dataInicio, dataFim);
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping("/criar-pastor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Pastor> criarPastor(@Valid @RequestBody CriarPastorDTO dto) {
        Pastor pastorCriado = pastorService.criarPastorCompleto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pastorCriado);
    }

    // ========================== ENDPOINTS ADICIONAIS PARA CÉLULAS ==========================

    @GetMapping("/celulas/{celulaId}/dashboard")
    @Transactional(readOnly = true)
    public ResponseEntity<CelulaDashboardDTO> dashboardCelula(@PathVariable Long celulaId) {
        Celula celula = celulaService.buscarPorId(celulaId);
        String nomeLider = celula.getLider() != null ? celula.getLider().getNome() : "Sem líder";

        int totalMembros = (int) membroRepository.countByCelulaIdAndArquivadoFalse(celulaId);

        CelulaDashboardDTO dto = CelulaDashboardDTO.withDefaults(
                celula.getId(),
                celula.getNome(),
                celula.getDiaSemana(),
                celula.getHorario(),
                celula.getEndereco(),
                nomeLider
        ).withAttendance(totalMembros, 0, 0.0);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/celulas/{celulaId}/frequencia")
    @Transactional
    public ResponseEntity<String> registrarFrequenciaCelula(
            @PathVariable Long celulaId,
            @Valid @RequestBody RegistrarFrequenciaCelulaDTO dto) {

        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new ResourceNotFoundException("Célula não encontrada"));

        presencaService.registrarPresencaCelula(dto.data(), dto.presentesIds(), celula);
        return ResponseEntity.ok("Frequência da célula registrada com sucesso!");
    }

    @GetMapping("/celulas/{celulaId}/crescimento")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CelulaCrescimentoDTO>> relatorioCrescimentoCelula(@PathVariable Long celulaId) {
        List<CelulaCrescimentoDTO> relatorio = celulaService.gerarRelatorioCrescimento(celulaId, 6);
        return ResponseEntity.ok(relatorio);
    }
    @GetMapping("/meu-perfil")
    public ResponseEntity<Map<String, Object>> meuPerfil(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("erro", "Não autenticado"));
        }

        Map<String, Object> perfil = new HashMap<>();
        perfil.put("nome", authentication.getName()); // geralmente o email
        perfil.put("principal", authentication.getPrincipal());
        perfil.put("authorities", authentication.getAuthorities()); // ← AQUI ESTÁ A ROLE!
        perfil.put("authenticated", authentication.isAuthenticated());

        return ResponseEntity.ok(perfil);
    }
    @PostMapping("/cadastrar-perfil-pastor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Pastor> cadastrarPerfilPastor(@Valid @RequestBody CadastrarPerfilPastorDTO dto) {

        LocalDate data = LocalDate.parse(dto.dataOrdenacao());

        Pastor pastor = pastorService.cadastrarPerfilPastor(
                dto.usuarioId(),
                data,
                dto.igrejaOrdenacao(),
                dto.pastorPrincipal()
        );

        return ResponseEntity.ok(pastor);
    }
    @PostMapping("/promover-para-pastor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Pastor> promoverParaPastor(@Valid @RequestBody PromoverParaPastorDTO dto) {

        LocalDate dataOrdenacao = LocalDate.parse(dto.dataOrdenacao());

        Pastor pastor = pastorService.promoverUsuarioAPastor(  // ou promoverMembroAPastor, dependendo do que você implementou
                dto.usuarioId(),
                dataOrdenacao,
                dto.igrejaOrdenacao(),
                dto.pastorPrincipal()
        );

        return ResponseEntity.ok(pastor);
    }
    @GetMapping("/pastores")
    public ResponseEntity<List<PastorResponseDTO>> listarPastores() {
        return ResponseEntity.ok(pastorService.listarTodosDTO());
    }



    @GetMapping("/pastores/{id}")
    public ResponseEntity<PastorResponseDTO> buscarPastor(@PathVariable Long id) {
        return ResponseEntity.ok(pastorService.buscarPorIdDTO(id));
    }


}