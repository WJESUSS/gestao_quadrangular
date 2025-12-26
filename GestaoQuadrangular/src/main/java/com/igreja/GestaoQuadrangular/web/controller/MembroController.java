package com.igreja.GestaoQuadrangular.web.controller;

import com.igreja.GestaoQuadrangular.application.dto.MembroAptoVotacaoDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.repository.MembroRepository;

import com.igreja.GestaoQuadrangular.servicce.EscolaBiblicaService;
import com.igreja.GestaoQuadrangular.servicce.MembroService;
import com.igreja.GestaoQuadrangular.servicce.VisitanteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/membros")
@PreAuthorize("isAuthenticated()") // Todos precisam estar logados
public class MembroController {

    private final MembroRepository membroRepository;
    private final VisitanteService visitanteService;
    private final MembroService service;
    private final EscolaBiblicaService escolaBiblicaService;

    public MembroController(
            MembroRepository membroRepository,
            VisitanteService visitanteService,
            MembroService service,
            EscolaBiblicaService escolaBiblicaService) {
        this.membroRepository = membroRepository;
        this.visitanteService = visitanteService;
        this.service = service;
        this.escolaBiblicaService = escolaBiblicaService;
    }

    // ====================== APENAS LEITURA (Líderes e Pastores podem ver) ======================

    @GetMapping
    public ResponseEntity<List<Membro>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Membro> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/aptos-votacao")
    public ResponseEntity<List<MembroAptoVotacaoDTO>> listarAptosVotacao() {
        List<Membro> membros = membroRepository.findAptosParaVotacao();
        List<MembroAptoVotacaoDTO> dtos = membros.stream()
                .map(m -> new MembroAptoVotacaoDTO(
                        m.getId(),
                        m.getNome(),
                        m.getStatusEspiritualColor(),
                        m.isAptoParaVotar() // usando o método da entidade
                ))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/aniversariantes/semana")
    public ResponseEntity<List<Membro>> aniversariantesSemana() {
        return ResponseEntity.ok(service.getAniversariantesDaSemana());
    }

    @GetMapping("/aniversariantes/mes")
    public ResponseEntity<List<Membro>> aniversariantesMes() {
        return ResponseEntity.ok(service.getAniversariantesDoMes());
    }

    @GetMapping("/inativos")
    public ResponseEntity<Page<Membro>> inativos(
            @RequestParam(defaultValue = "3") int meses,
            Pageable pageable) {
        return ResponseEntity.ok(service.getMembrosInativos(meses, pageable));
    }

    // ====================== CONVERSÃO DE VISITANTE (Líder pode fazer) ======================
    @PostMapping("/{id}/converter")
    @PreAuthorize("hasRole('LIDER') or hasRole('PASTOR') or hasRole('ADMIN')")
    public ResponseEntity<Membro> converterVisitante(@PathVariable Long id) {
        Membro membroConvertido = visitanteService.converterParaMembro(id);
        return ResponseEntity.ok(membroConvertido);
    }

    // REMOVIDOS intencionalmente:
    // POST /membros → só pastor
    // PUT /membros/{id} → só pastor
    // PATCH /archive → só pastor
    // DELETE → só pastor
}