package com.igreja.GestaoQuadrangular.web.controller;

import com.igreja.GestaoQuadrangular.application.dto.ApresentacaoCriancaRequestDTO;
import com.igreja.GestaoQuadrangular.application.dto.ApresentacaoCriancaResponseDTO;
import com.igreja.GestaoQuadrangular.servicce.ApresentacaoCriancaService;
import com.igreja.GestaoQuadrangular.servicce.ApresentacaoCriancaService; // ← corrigido: "service"
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/secretaria/apresentacao-crianca")
@PreAuthorize("hasRole('SECRETARIA') or hasRole('PASTOR')")
public class ApresentacaoCriancaController {

    private final ApresentacaoCriancaService service;

    public ApresentacaoCriancaController(ApresentacaoCriancaService service) {
        this.service = service;
    }

    // 1. CADASTRAR nova apresentação (POST)
    @PostMapping
    public ResponseEntity<ApresentacaoCriancaResponseDTO> cadastrar(
            @Valid @RequestBody ApresentacaoCriancaRequestDTO request) {

        ApresentacaoCriancaResponseDTO response = service.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. LISTAR próximas apresentações (GET opcional)
    @GetMapping("/proximas")
    public ResponseEntity<List<ApresentacaoCriancaResponseDTO>> listarProximas() {
        List<ApresentacaoCriancaResponseDTO> lista = service.listarProximas();
        return ResponseEntity.ok(lista);
    }

    // 3. BAIXAR certificado PDF (GET)
    @GetMapping("/certificado-pdf/{apresentacaoId}")
    public ResponseEntity<byte[]> gerarCertificado(@PathVariable Long apresentacaoId) throws Exception {
        byte[] pdf = service.gerarCertificadoPdf(apresentacaoId);

        String filename = "certificado_apresentacao_" + apresentacaoId + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}