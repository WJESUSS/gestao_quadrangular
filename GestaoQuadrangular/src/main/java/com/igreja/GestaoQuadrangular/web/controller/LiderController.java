package com.igreja.GestaoQuadrangular.web.controller;

import com.igreja.GestaoQuadrangular.application.dto.MembroResponseDTO;
import com.igreja.GestaoQuadrangular.application.dto.RegistrarPresencaDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.num.StatusEspiritual; // ← o service que criamos antes
import com.igreja.GestaoQuadrangular.servicce.LiderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lider")
@PreAuthorize("hasRole('LIDER')")  // Só líderes acessam esse módulo
public class LiderController {

   private final LiderService liderService;

    public LiderController(LiderService liderService) {
        this.liderService = liderService;
    }


    // Dashboard resumido da célula do líder logado
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboardMinhaCelula() {
        return ResponseEntity.ok(liderService.dashboardMinhaCelula());
    }

    // Listar todos os membros da minha célula (com DTO rico)
    @GetMapping("/membros")
    public ResponseEntity<List<MembroResponseDTO>> listarMembros() {
        return ResponseEntity.ok(liderService.listarMembrosMinhaCelula());
    }

    // Atualizar status espiritual de um membro da minha célula
    @PatchMapping("/membros/{id}/status")
    public ResponseEntity<Membro> atualizarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        StatusEspiritual novoStatus = StatusEspiritual.valueOf(request.get("status"));
        return ResponseEntity.ok(liderService.atualizarStatusMembro(id, novoStatus));
    }

    // Arquivar um membro da minha célula
    @PatchMapping("/membros/{id}/arquivar")
    public ResponseEntity<Membro> arquivarMembro(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {

        boolean arquivar = request.getOrDefault("arquivar", true);
        return ResponseEntity.ok(liderService.arquivarMembro(id, arquivar));
    }
    @GetMapping("/teste-auth")
    public String testeAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Usuário: " + auth.getName() + " | Roles: " + auth.getAuthorities();
    }
    @PostMapping("/presenca/celula")
    public ResponseEntity<String> registrarPresencaCelula(
            @RequestBody RegistrarPresencaDTO dto,
            Authentication auth) {

        liderService.registrarPresencaCelula(dto.data(), dto.presentesIds());
        return ResponseEntity.ok("Presença na célula registrada com sucesso!");
    }
    // Futuros endpoints:
    // @PostMapping("/presenca") → registrar chamada
    // @GetMapping("/relatorio/faltas") → membros com mais faltas
}