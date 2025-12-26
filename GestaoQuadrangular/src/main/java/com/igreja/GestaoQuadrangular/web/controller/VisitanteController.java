package com.igreja.GestaoQuadrangular.web.controller; // ajuste o package conforme o seu projeto

import com.igreja.GestaoQuadrangular.application.dto.VisitanteResponseDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.entity.Visitante;
import com.igreja.GestaoQuadrangular.servicce.VisitanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/visitantes")
@RequiredArgsConstructor
public class VisitanteController {

    private final VisitanteService visitanteService;

    // ==================== DTO para cadastro ====================
    public record CadastrarVisitanteRequest(
            String nome,
            String telefone,
            String email,
            Long celulaId,
            Long usuarioId,
            String origem,
            String evento
    ) {}

    // ==================== 1. CADASTRAR VISITANTE ====================
    @PostMapping
    public ResponseEntity<VisitanteResponseDTO> cadastrarVisitante(
            @Valid @RequestBody CadastrarVisitanteRequest request) {

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


    // ==================== 2. CONVERTER VISITANTE EM MEMBRO ====================
    @PostMapping("/{id}/converter-membro")
    public ResponseEntity<Membro> converterParaMembro(@PathVariable Long id) {
        Membro membro = visitanteService.converterParaMembro(id);
        return ResponseEntity.ok(membro);
    }

    // ==================== OPCIONAL: MARCAR NOVA VISITA ====================
    @PostMapping("/{id}/nova-visita")
    public ResponseEntity<Void> registrarNovaVisita(@PathVariable Long id) {
        visitanteService.registrarNovaVisita(id);
        return ResponseEntity.ok().build();
    }

    // ==================== OPCIONAL: RELATÓRIOS ====================
    @GetMapping("/relatorio/ultimos-30-dias")
    public ResponseEntity<Long> totalVisitantesUltimos30Dias() {
        return ResponseEntity.ok(visitanteService.totalVisitantesUltimos30Dias());
    }

    @GetMapping("/relatorio/total-convertidos")
    public ResponseEntity<Long> totalConvertidos() {
        return ResponseEntity.ok(visitanteService.totalConvertidos());
    }
    // ==================== LISTAR TODOS OS VISITANTES ====================
    @GetMapping("/lista")
    public ResponseEntity<List<VisitanteResponseDTO>> listarTodos() {
        return ResponseEntity.ok(visitanteService.listarTodos());
    }


}