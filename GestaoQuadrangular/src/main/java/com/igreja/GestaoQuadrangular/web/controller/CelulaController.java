package com.igreja.GestaoQuadrangular.web.controller;



import com.igreja.GestaoQuadrangular.application.dto.CelulaCreateDTO;
import com.igreja.GestaoQuadrangular.application.dto.CelulaDashboardDTO;
import com.igreja.GestaoQuadrangular.application.dto.RelatorioDiscipuladoDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.repository.CelulaRepository;
import com.igreja.GestaoQuadrangular.servicce.CelulaDashboardService;
import com.igreja.GestaoQuadrangular.servicce.CelulaService;
import com.igreja.GestaoQuadrangular.servicce.DiscipuladoService;
import com.igreja.GestaoQuadrangular.servicce.LeaderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/celula")
public class CelulaController {

    private final CelulaDashboardService celulaDashboardService;
    private final CelulaService celulaService;
    private final LeaderService leaderService;
    private final CelulaRepository celulaRepository;
    private final DiscipuladoService discipuladoService;

    public CelulaController(CelulaDashboardService celulaDashboardService, CelulaService celulaService, LeaderService leaderService, CelulaRepository celulaRepository, DiscipuladoService discipuladoService) {
        this.celulaDashboardService = celulaDashboardService;
        this.celulaService = celulaService;
        this.leaderService = leaderService;
        this.celulaRepository = celulaRepository;
        this.discipuladoService = discipuladoService;
    }

    // ========================================
    // ENDPOINTS EXCLUSIVOS PARA O PASTOR
    // ========================================

    @PreAuthorize("hasRole('PASTOR')")
    @PostMapping
    public ResponseEntity<Celula> criarCelula(@RequestBody CelulaCreateDTO dto) {
        Celula celula = celulaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(celula);
    }

    // Opcional: Listar todas as células (útil para painel administrativo do pastor)
    @PreAuthorize("hasRole('PASTOR')")
    @GetMapping
    public ResponseEntity<?> listarTodas() {
        return ResponseEntity.ok(celulaService.listarTodas());
    }

    // ========================================
    // ENDPOINTS COMPARTILHADOS / LÍDER DA CÉLULA
    // ========================================

    /**
     * Envia o relatório da célula por e-mail para o pastor.
     * Acessível para:
     * - Pastor (sempre)
     * - Líder da célula específica
     */
    @PreAuthorize("hasRole('PASTOR') or " +
            "(hasRole('LIDER') and @securityService.isLeaderOfCell(authentication, #celulaId))")
    @PostMapping("/relatorio/enviar/{celulaId}")
    public ResponseEntity<String> enviarRelatorioParaPastor(
            @PathVariable Long celulaId,
            @RequestParam String emailPastor) {

        leaderService.enviarRelatorioPorEmail(celulaId, emailPastor);
        return ResponseEntity.ok("Relatório enviado com sucesso!");
    }

    /**
     * Busca os dados de uma célula específica.
     * Usado como base para o dashboard do líder.
     */
    @PreAuthorize("hasRole('PASTOR') or " +
            "(hasRole('LIDER') and @securityService.isLeaderOfCell(authentication, #celulaId))")
    @GetMapping("/{celulaId}")
    public ResponseEntity<Celula> buscarCelula(@PathVariable Long celulaId) {
        Celula celula = celulaService.buscarPorId(celulaId);
        return ResponseEntity.ok(celula);
    }
    @GetMapping("/{celulaId}/dashboard")
    public ResponseEntity<CelulaDashboardDTO> getDashboard(@PathVariable Long celulaId) {
        CelulaDashboardDTO dashboard = celulaDashboardService.getDashboard(celulaId); // ← ERRO aqui
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
    @PreAuthorize("hasRole('PASTOR')")
    public ResponseEntity<List<RelatorioDiscipuladoDTO>> relatorioDiscipulado(@PathVariable Long celulaId) {
        return ResponseEntity.ok(discipuladoService.relatorioPorCelula(celulaId));
    }
    // Você pode adicionar mais endpoints aqui no futuro:
    // - GET /{celulaId}/dashboard → retorna DTO com estatísticas
    // - GET /{celulaId}/membros
    // - POST /{celulaId}/frequencia → registrar presença
    // etc.
}