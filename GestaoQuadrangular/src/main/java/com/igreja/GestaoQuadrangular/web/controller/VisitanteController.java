package com.igreja.GestaoQuadrangular.web.controller;

import com.igreja.GestaoQuadrangular.application.dto.VisitanteResponseDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.entity.Visitante;
import com.igreja.GestaoQuadrangular.servicce.VisitanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitantes")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class VisitanteController {

    private final VisitanteService visitanteService;

    // ==================== DTO ====================
    public record CadastrarVisitanteRequest(
            String nome,
            String telefone,
            String email,
            Long celulaId,
            Long usuarioId,
            String origem,
            String evento
    ) {}

    // ==================== CADASTRAR VISITANTE ====================
    @PostMapping
    public ResponseEntity<VisitanteResponseDTO> cadastrarVisitante(
            @RequestBody CadastrarVisitanteRequest request) {

        Visitante visitante = visitanteService.cadastrarVisitante(
                request.nome(),
                request.telefone(),
                request.email(),
                request.celulaId(),
                request.usuarioId(),
                request.origem(),
                request.evento()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(VisitanteResponseDTO.fromEntity(visitante));
    }

    // ==================== CONVERTER EM MEMBRO ====================
    @PreAuthorize("hasRole('LIDER') or hasRole('PASTOR') or hasRole('ADMIN')")
    @PostMapping("/{id}/converter-membro")
    public ResponseEntity<Membro> converterParaMembro(@PathVariable Long id) {
        return ResponseEntity.ok(visitanteService.converterParaMembro(id));
    }

    // ==================== NOVA VISITA ====================
    @PreAuthorize("hasRole('LIDER') or hasRole('PASTOR') or hasRole('ADMIN')")
    @PostMapping("/{id}/nova-visita")
    public ResponseEntity<Void> registrarNovaVisita(@PathVariable Long id) {
        visitanteService.registrarNovaVisita(id);
        return ResponseEntity.ok().build();
    }

    // ==================== LISTAR ====================
    @PreAuthorize("hasRole('LIDER') or hasRole('PASTOR') or hasRole('ADMIN')")
    @GetMapping("/lista")
    public ResponseEntity<List<VisitanteResponseDTO>> listarTodos() {
        return ResponseEntity.ok(visitanteService.listarTodos());
    }

    @PreAuthorize("hasRole('LIDER') or hasRole('PASTOR') or hasRole('ADMIN')")
    @GetMapping("/celula/{celulaId}")
    public ResponseEntity<List<VisitanteResponseDTO>> listarPorCelula(@PathVariable Long celulaId) {
        return ResponseEntity.ok(visitanteService.listarPorCelula(celulaId));
    }

}
