package com.igreja.GestaoQuadrangular.web.controller;

import com.igreja.GestaoQuadrangular.application.dto.ContribuicaoCreateDTO;
import com.igreja.GestaoQuadrangular.application.dto.RelatorioFinanceiroDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Contribuicao;
import com.igreja.GestaoQuadrangular.servicce.TesourariaService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/tesouraria")
@PreAuthorize("hasRole('PASTOR') or hasRole('TESOUREIRO')")
@CrossOrigin(origins = "http://localhost:5173")
public class TesourariaController {

    private final TesourariaService tesourariaService;

    public TesourariaController(TesourariaService tesourariaService) {
        this.tesourariaService = tesourariaService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<Contribuicao> registrar(@RequestBody ContribuicaoCreateDTO dto) {
        Contribuicao saved = tesourariaService.registrar(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/relatorio")
    public ResponseEntity<RelatorioFinanceiroDTO> relatorio(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {
        return ResponseEntity.ok(tesourariaService.gerarRelatorio(inicio, fim));
    }

    @GetMapping("/relatorio/pdf")
    public ResponseEntity<byte[]> relatorioPdf(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {
        byte[] pdf = tesourariaService.gerarPdfRelatorio(inicio, fim);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=relatorio_financeiro_" +
                        inicio + "_a_" + fim + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}