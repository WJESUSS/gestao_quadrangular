package com.igreja.GestaoQuadrangular.web.controller;

import com.igreja.GestaoQuadrangular.application.dto.MembroDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.entity.TransferenciaMembresia;
import com.igreja.GestaoQuadrangular.domain.repository.TransferenciaRepository;
import com.igreja.GestaoQuadrangular.servicce.SecretariaService;
import com.igreja.GestaoQuadrangular.servicce.SecretariaService; // ← corrigido: "service" (não "servicce")
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/secretaria/avancado")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('SECRETARIA') or hasRole('PASTOR') or hasRole('ADMIN')")

public class SecretariaAvancadaController {

    private final TransferenciaRepository transferenciaRepository;
    private final SecretariaService secretariaService;

    public SecretariaAvancadaController(TransferenciaRepository transferenciaRepository,
                                        SecretariaService secretariaService) {
        this.transferenciaRepository = transferenciaRepository;
        this.secretariaService = secretariaService;
    }

    @GetMapping("/carta-transferencia-pdf/{transferenciaId}")
    public ResponseEntity<byte[]> gerarCartaPdf(@PathVariable Long transferenciaId) throws Exception {
        TransferenciaMembresia transferencia = transferenciaRepository
                .findById(transferenciaId)
                .orElseThrow(() -> new RuntimeException("Transferência não encontrada: " + transferenciaId));

        byte[] pdf = secretariaService.gerarPdfCartaTransferencia(transferencia);

        String nomeArquivo = "carta_transferencia_" + transferencia.getMembro().getNome().replaceAll("\\s+", "_") + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nomeArquivo)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/aniversariantes-mes/{mes}")
    public List<MembroDTO> aniversariantesMes(@PathVariable int mes) {
        List<Membro> membros = secretariaService.listarAniversariantesMes(mes);
        return membros.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converte entidade Membro para MembroDTO
     */
    private MembroDTO toDTO(Membro membro) {
        if (membro == null) {
            return null;
        }

        String nomeCelula = null;
        String nomeLiderCelula = null;

        if (membro.getCelula() != null) {
            nomeCelula = membro.getCelula().getNome();
            if (membro.getCelula().getLider() != null) {
                nomeLiderCelula = membro.getCelula().getLider().getNome();
            }
        }

        return new MembroDTO(
                membro.getId(),
                membro.getNome(),
                membro.getEmail(),
                membro.getTelefone(),
                nomeCelula,
                nomeLiderCelula,
                membro.getDataUltimaAtualizacaoEscada(),
                membro.getEscadaSucesso(),
                membro.getStatus(),
                membro.getFaltasConsecutivas(),
                membro.isArquivado(),
                membro.getObservacaoDiscipulado()
        );
    }
}