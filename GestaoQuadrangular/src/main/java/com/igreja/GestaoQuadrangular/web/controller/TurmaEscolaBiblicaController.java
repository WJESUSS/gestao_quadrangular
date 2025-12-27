package com.igreja.GestaoQuadrangular.web.controller;

import com.igreja.GestaoQuadrangular.application.dto.TurmaEscolaBiblicaDTO;
import com.igreja.GestaoQuadrangular.domain.entity.TurmaEscolaBiblica;
import com.igreja.GestaoQuadrangular.servicce.EscolaBiblicaService;
import com.igreja.GestaoQuadrangular.servicce.EscolaBiblicaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/turmas")
@CrossOrigin(origins = "http://localhost:5173")
public class TurmaEscolaBiblicaController {

    private final EscolaBiblicaService escolaBiblicaService;

    public TurmaEscolaBiblicaController(EscolaBiblicaService escolaBiblicaService) {
        this.escolaBiblicaService = escolaBiblicaService;
    }

    // ==================== LISTAR TURMAS ====================

    /**
     * Lista todas as turmas abertas (com data de fim no futuro ou sem data de fim)
     */
    @GetMapping("/abertas")
    public ResponseEntity<List<TurmaEscolaBiblicaDTO>> listarTurmasAbertas() {
        List<TurmaEscolaBiblica> turmas = escolaBiblicaService.listarTurmasAbertas();
        return ResponseEntity.ok(escolaBiblicaService.toTurmaDTOList(turmas));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<TurmaEscolaBiblicaDTO>> listarPorTipo(@PathVariable TurmaEscolaBiblica.TipoTurma tipo) {
        List<TurmaEscolaBiblica> turmas = escolaBiblicaService.listarTurmasPorTipo(tipo);
        return ResponseEntity.ok(escolaBiblicaService.toTurmaDTOList(turmas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaEscolaBiblicaDTO> buscarPorId(@PathVariable Long id) {
        TurmaEscolaBiblica turma = escolaBiblicaService.getTurmaPorId(id);
        return ResponseEntity.ok(escolaBiblicaService.toTurmaDTO(turma));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TurmaEscolaBiblicaDTO> criarTurma(@Valid @RequestBody CriarTurmaRequest request) {
        TurmaEscolaBiblica turma = escolaBiblicaService.criarTurma(
                request.nome(),
                request.tipoTurma(),
                request.dataInicio(),
                request.dataFim(),
                request.professor(),
                request.capacidadeMaxima()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(escolaBiblicaService.toTurmaDTO(turma));
    }
    // ==================== INSCRIÇÃO EM TURMA ====================

    /**
     * Inscreve um membro em uma turma
     */
    @PostMapping("/{turmaId}/inscrever/{membroId}")
    public ResponseEntity<?> inscreverMembro(
            @PathVariable Long turmaId,
            @PathVariable Long membroId) {
        escolaBiblicaService.inscreverMembroEmTurma(membroId, turmaId);
        return ResponseEntity.ok("Membro inscrito com sucesso na turma!");
    }

    /**
     * Remove inscrição de um membro
     */
    @DeleteMapping("/{turmaId}/inscrever/{membroId}")
    public ResponseEntity<?> removerInscricao(
            @PathVariable Long turmaId,
            @PathVariable Long membroId) {
        escolaBiblicaService.removerInscricao(membroId, turmaId);
        return ResponseEntity.ok("Inscrição removida com sucesso.");
    }
    @PostMapping("/membros/{id}/historico/batismo")
    public ResponseEntity<?> registrarBatismo(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String pastor = body.get("pastor");
        escolaBiblicaService.registrarBatismo(id, pastor);
        return ResponseEntity.ok("Batismo registrado com sucesso");
    }


    // ==================== REQUEST DTO PARA CRIAÇÃO ====================

    public record CriarTurmaRequest(
            String nome,
            TurmaEscolaBiblica.TipoTurma tipoTurma,
            LocalDate dataInicio,
            LocalDate dataFim,
            String professor,
            int capacidadeMaxima
    ) {

    }
}