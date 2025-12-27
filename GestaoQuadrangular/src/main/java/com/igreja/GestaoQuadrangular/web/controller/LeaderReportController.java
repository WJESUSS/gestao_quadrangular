package com.igreja.GestaoQuadrangular.web.controller;

import com.igreja.GestaoQuadrangular.application.dto.CelulaReportDTO;
import com.igreja.GestaoQuadrangular.servicce.LeaderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leader/reports")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('LIDER') or hasRole('PASTOR') or hasRole('ADMIN')")
public class LeaderReportController {

    private final LeaderService leaderService;

    public LeaderReportController(LeaderService leaderService) {
        this.leaderService = leaderService;
    }

    // Relatório em JSON
    @GetMapping("/{celulaId}")
    public ResponseEntity<CelulaReportDTO> gerarRelatorioJson(@PathVariable Long celulaId) {
        CelulaReportDTO relatorio = leaderService.gerarRelatorio(celulaId);
        return ResponseEntity.ok(relatorio);
    }

    // Relatório em PDF
    @GetMapping("/{celulaId}/pdf")
    public ResponseEntity<byte[]> gerarRelatorioPdf(@PathVariable Long celulaId) {
        byte[] pdfBytes = leaderService.gerarPdf(celulaId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_celula.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
