package com.igreja.GestaoQuadrangular.servicce;

import com.igreja.GestaoQuadrangular.application.dto.VisitanteResponseDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.entity.Usuario;
import com.igreja.GestaoQuadrangular.domain.entity.Visitante;
import com.igreja.GestaoQuadrangular.domain.repository.CelulaRepository;
import com.igreja.GestaoQuadrangular.domain.repository.MembroRepository;
import com.igreja.GestaoQuadrangular.domain.repository.UsuarioRepository;
import com.igreja.GestaoQuadrangular.domain.repository.VisitanteRepository;
import com.igreja.GestaoQuadrangular.web.exception.VisitanteJaCadastradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitanteService {

    private final VisitanteRepository visitanteRepository;
    private final MembroRepository membroRepository;
    private final CelulaRepository celulaRepository;
    private final UsuarioRepository usuarioRepository;

    // =========================
    // CADASTRAR VISITANTE
    // =========================
    public Visitante cadastrarVisitante(
            String nome,
            String telefone,
            String email,
            Long celulaId,
            Long usuarioId,
            String origem,
            String evento
    ) {
        if (nome == null || nome.isBlank()) {
            throw new RuntimeException("Nome é obrigatório");
        }

        if (celulaId == null) {
            throw new RuntimeException("Célula é obrigatória");
        }

        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));

        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        }

        visitanteRepository
                .findFirstByNomeIgnoreCaseAndTelefone(nome, telefone)
                .ifPresent(v -> {
                    throw new VisitanteJaCadastradoException();
                });

        Visitante visitante = Visitante.builder()
                .nome(nome)
                .telefone(telefone)
                .email(email)
                .celula(celula)
                .registradoPor(usuario)
                .origem(origem)
                .eventoFrequentado(evento)
                .status(Visitante.StatusVisitante.NOVO)
                .dataPrimeiraVisita(LocalDate.now())
                .visitasCount(1)
                .ehVisitante(true)
                .build();

        return visitanteRepository.save(visitante);
    }

    // =========================
    // NOVA VISITA
    // =========================
    public void registrarNovaVisita(Long visitanteId) {
        Visitante visitante = visitanteRepository.findById(visitanteId)
                .orElseThrow(() -> new RuntimeException("Visitante não encontrado"));

        visitante.incrementarVisita();

        if (visitante.getVisitasCount() > 1) {
            visitante.setStatus(Visitante.StatusVisitante.RECORRENTE);
        }
    }

    // =========================
    // CONVERTER EM MEMBRO
    // =========================
    public Membro converterParaMembro(Long visitanteId) {
        Visitante visitante = visitanteRepository.findById(visitanteId)
                .orElseThrow(() -> new RuntimeException("Visitante não encontrado"));

        if (visitante.getStatus() == Visitante.StatusVisitante.CONVERTIDO) {
            throw new RuntimeException("Visitante já convertido");
        }

        Membro membro = Membro.builder()
                .nome(visitante.getNome())
                .telefone(visitante.getTelefone())
                .email(visitante.getEmail())
                .celula(visitante.getCelula())
                .dataEntradaCelula(LocalDate.now())
                .ativo(true)
                .build();

        membroRepository.save(membro);

        visitante.setStatus(Visitante.StatusVisitante.CONVERTIDO);
        visitante.setConvertidoParaMembro(membro);

        return membro;
    }

    // =========================
    // LISTAGENS
    // =========================
    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> listarTodos() {
        return visitanteRepository.findAll()
                .stream()
                .map(VisitanteResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> listarPorCelula(Long celulaId) {
        return visitanteRepository.findByCelulaId(celulaId)
                .stream()
                .map(VisitanteResponseDTO::fromEntity)
                .toList();
    }

    // =========================
    // RELATÓRIOS
    // =========================
    public long totalVisitantesUltimos30Dias() {
        return visitanteRepository.countByDataPrimeiraVisitaAfter(LocalDate.now().minusDays(30));
    }

    public long totalConvertidos() {
        return visitanteRepository.findByStatus(Visitante.StatusVisitante.CONVERTIDO).size();
    }
}
