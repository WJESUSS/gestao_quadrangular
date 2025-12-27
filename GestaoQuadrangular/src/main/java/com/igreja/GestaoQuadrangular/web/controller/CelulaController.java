package com.igreja.GestaoQuadrangular.web.controller;



import com.igreja.GestaoQuadrangular.application.dto.*;
import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.entity.Usuario;
import com.igreja.GestaoQuadrangular.domain.repository.CelulaRepository;
import com.igreja.GestaoQuadrangular.domain.repository.RelatorioSemanalRepository;
import com.igreja.GestaoQuadrangular.num.TipoReuniao;
import com.igreja.GestaoQuadrangular.servicce.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/presenca")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('SECRETARIO') or hasRole('PASTOR') or hasRole('ADMIN')")
public class CelulaController {

    private final PresencaService presencaService;
    private  final RelatorioSemanalRepository relatorioSemanalRepository;
    private final CelulaDashboardService celulaDashboardService;
    private final CelulaService celulaService;
    private final LeaderService leaderService;
    private final CelulaRepository celulaRepository;
    private final DiscipuladoService discipuladoService;

    public CelulaController(PresencaService presencaService, RelatorioSemanalRepository relatorioSemanalRepository, CelulaDashboardService celulaDashboardService,
                            CelulaService celulaService,
                            LeaderService leaderService,
                            CelulaRepository celulaRepository,
                            DiscipuladoService discipuladoService) {
        this.presencaService = presencaService;
        this.relatorioSemanalRepository = relatorioSemanalRepository;
        this.celulaDashboardService = celulaDashboardService;
        this.celulaService = celulaService;
        this.leaderService = leaderService;
        this.celulaRepository = celulaRepository;
        this.discipuladoService = discipuladoService;
    }

    // ========================================
    // CRIAR CÉLULA
    // ========================================
    @PostMapping
    public ResponseEntity<Celula> criarCelula(@RequestBody CelulaCreateDTO dto) {
        Celula celula = celulaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(celula);
    }

    // ========================================
    // LISTAR TODAS AS CÉLULAS (front-end admin)
    // ========================================

    @GetMapping // → essa é a URL que o front-end chama: /api/celula
    public ResponseEntity<List<CelulaDTO>> listarTodasDTO() {
        List<CelulaDTO> celulas = celulaService.listarTodasDTO();
        return ResponseEntity.ok(celulas);
    }

    // ========================================
    // ADICIONAR MEMBRO À CÉLULA
    // ========================================
    @PostMapping("/adicionar-membro-celula")
    @PreAuthorize("hasRole('PASTOR') or hasRole('LIDER') or hasRole('ADMIN') or hasRole('SECRETARIO')")
    public ResponseEntity<String> adicionarMembroACelula(
            @Valid @RequestBody AdicionarMembroCelulaDTO dto,
            Authentication authentication) {

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        celulaService.adicionarMembroACelula(dto.membroId(), dto.celulaId(), usuarioLogado);
        return ResponseEntity.ok("Membro adicionado à célula com sucesso!");
    }

    // ========================================
    // DASHBOARD E RELATÓRIOS
    @GetMapping("/{celulaId}")
    public ResponseEntity<CelulaDTO> buscarCelula(@PathVariable Long celulaId) {
        // O retorno deve ser CelulaDTO e NÃO Celula (entidade)
        return ResponseEntity.ok(celulaService.buscarPorIdDTO(celulaId));
    }
    @GetMapping("/{celulaId}/dashboard")
    public ResponseEntity<CelulaDashboardDTO> getDashboard(@PathVariable Long celulaId) {
        CelulaDashboardDTO dashboard = celulaDashboardService.getDashboard(celulaId);
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping("/reset-alerta-multiplicacao/{celulaId}")
    @PreAuthorize("hasRole('PASTOR')")
    public ResponseEntity<String> resetAlerta(@PathVariable Long celulaId) {
        Celula celula = celulaRepository.findById(celulaId).orElseThrow();
        celula.setAlertaMultiplicacaoEnviado(false);
        celulaRepository.save(celula);
        return ResponseEntity.ok("Alerta resetado com sucesso!");
    }

    @GetMapping("/relatorio-discipulado/{celulaId}")
    @PreAuthorize("hasRole('PASTOR')or hasRole('LIDER') or hasRole('ADMIN')")
    public ResponseEntity<List<RelatorioDiscipuladoDTO>> relatorioDiscipulado(@PathVariable Long celulaId) {
        return ResponseEntity.ok(discipuladoService.relatorioPorCelula(celulaId));
    }

    @GetMapping("/usuarios/nao-na-celula")
    public ResponseEntity<List<Usuario>> listarUsuariosNaoNaCelula() {
        List<Usuario> usuarios = celulaService.listarUsuariosNaoNaCelula();
        return ResponseEntity.ok(usuarios);
    }
    @PreAuthorize("hasAnyRole('LIDER', 'PASTOR', 'ADMIN')")
    @PostMapping("/{id}/relatorio") // Removi o "-semanal" para bater com o log
    public ResponseEntity<Void> salvarRelatorioSemanal(@PathVariable Long id,
                                                       @RequestBody RelatorioSemanalCreateDTO dto,
                                                       @AuthenticationPrincipal Usuario usuario) {
        dto.setCelulaId(id);
        celulaService.salvarRelatorioSemanal(dto, usuario);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/relatorios/consolidado")
    public ResponseEntity<Map<String, Object>> relatorioConsolidado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        Map<String, Object> dados = new HashMap<>();
        dados.put("totalConversoes", relatorioSemanalRepository.somarConversoesNoPeriodo(dataInicio, dataFim));
        dados.put("totalBatismos", relatorioSemanalRepository.somarBatismosNoPeriodo(dataInicio, dataFim));
        dados.put("mediaPresentes", relatorioSemanalRepository.mediaPresentesNoPeriodo(dataInicio, dataFim));
        dados.put("celulasAtivas", relatorioSemanalRepository.countCelulasComRelatorioNoPeriodo(dataInicio, dataFim));

        return ResponseEntity.ok(dados);
    }

    @GetMapping("/{celulaId}/relatorio")
    @PreAuthorize("hasRole('PASTOR') or hasRole('ADMIN') or hasRole('SECRETARIO')")
    public ResponseEntity<?> obterRelatorioPorData(
            @PathVariable Long celulaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        // Busca o relatório no banco (você deve ter esse método no seu service)
        // Se o relatório não existir, o service deve retornar Optional.empty()
        Optional<RelatorioPorDataDTO> relatorio = celulaService.buscarRelatorioPorData(celulaId, data);

        if (relatorio.isPresent()) {
            return ResponseEntity.ok(relatorio.get());
        }

        // RETORNA 200 OK VAZIO (Isso limpa o erro 404 do console)
        return ResponseEntity.ok().build();
    }
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
    @PutMapping("/discipulado/atualizar")
    public ResponseEntity<?> atualizarEtapaDiscipulado(@RequestBody AtualizarEtapaDTO dto) {
        try {
            celulaService.atualizarEtapaMembro(dto);
            return ResponseEntity.ok().body("{\"message\": \"Etapa atualizada com sucesso!\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("{\"error\": \"Etapa inválida: " + dto.etapaAtual() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Erro interno ao atualizar\"}");
        }
    }
    @GetMapping("/culto/lista-presenca") // Caminho alterado para não conflitar
    public ResponseEntity<List<PresencaResponseDTO>> listarPresencas(
            @RequestParam TipoReuniao tipoReuniao,
            @RequestParam LocalDate data) {
        List<PresencaResponseDTO> presencas = presencaService.listarPresencasPorTipoEData(tipoReuniao, data);
        return ResponseEntity.ok(presencas);
    }
}
