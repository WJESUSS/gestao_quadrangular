package com.igreja.GestaoQuadrangular.web.controller;

import com.igreja.GestaoQuadrangular.application.dto.*;
import com.igreja.GestaoQuadrangular.domain.entity.RelatorioSemanal;
import com.igreja.GestaoQuadrangular.domain.entity.MetaCelula;
import com.igreja.GestaoQuadrangular.domain.entity.MensagemChat;
import com.igreja.GestaoQuadrangular.domain.entity.Usuario;
import com.igreja.GestaoQuadrangular.servicce.CelulaService;  // ← Pacote correto: service
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/celula")
@CrossOrigin(origins = "http://localhost:5173")
public class CelulaExtraController {

    private final CelulaService celulaService;

    public CelulaExtraController(CelulaService celulaService) {
        this.celulaService = celulaService;
    }

    // ==================== RELATÓRIO SEMANAL ====================

    /**
     * POST /api/celulas/{celulaId}/relatorio-semanal
     * Líder envia relatório semanal simplificado
     */

    /**
     * GET /api/celulas/{celulaId}/relatorios-semanais?limite=10
     */
    @GetMapping("/{celulaId}/relatorios-semanais")
    public ResponseEntity<List<RelatorioSemanalListDTO>> listarRelatoriosSemanais(
            @PathVariable Long celulaId,
            @RequestParam(defaultValue = "10") int limite) {

        List<RelatorioSemanalListDTO> relatorios = celulaService.listarRelatoriosSemanais(celulaId, limite);
        return ResponseEntity.ok(relatorios);
    }

    // ==================== METAS PERSONALIZADAS ====================

    /**
     * POST /api/celulas/{celulaId}/meta
     */
    @PostMapping("/{celulaId}/meta")
    @PreAuthorize("hasRole('LIDER')")
    public ResponseEntity<MetaCelula> definirMeta(
            @PathVariable Long celulaId,
            @Valid @RequestBody MetaCelulaCreateDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        dto.setCelulaId(celulaId);
        MetaCelula meta = celulaService.definirMetaCelula(dto, usuarioLogado);
        return ResponseEntity.ok(meta);
    }

    /**
     * GET /api/celulas/{celulaId}/meta/progresso
     */
    @GetMapping("/{celulaId}/meta/progresso")
    public ResponseEntity<MetaProgressoDTO> obterProgressoMeta(
            @PathVariable Long celulaId) {

        MetaProgressoDTO progresso = celulaService.obterProgressoMeta(celulaId);
        return ResponseEntity.ok(progresso);
    }

    // ==================== CHAT INTERNO DA CÉLULA ====================

    /**
     * POST /api/celulas/{celulaId}/chat
     * Membro da célula envia mensagem
     */
    @PostMapping("/{celulaId}/chat")
    public ResponseEntity<MensagemChat> enviarMensagemChat(
            @PathVariable Long celulaId,
            @Valid @RequestBody MensagemChatCreateDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        dto.setCelulaId(celulaId);
        MensagemChat mensagem = celulaService.enviarMensagem(dto, usuarioLogado);
        return new ResponseEntity<>(mensagem, HttpStatus.CREATED);
    }

    /**
     * GET /api/celulas/{celulaId}/chat?limite=50
     */
    @GetMapping("/{celulaId}/chat")
    public ResponseEntity<List<MensagemChatListDTO>> listarMensagensChat(
            @PathVariable Long celulaId,
            @RequestParam(defaultValue = "50") int limite) {

        List<MensagemChatListDTO> mensagens = celulaService.listarMensagensChat(celulaId, limite);
        return ResponseEntity.ok(mensagens);
    }
}