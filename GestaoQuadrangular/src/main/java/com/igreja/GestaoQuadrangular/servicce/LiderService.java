package com.igreja.GestaoQuadrangular.servicce;

import com.igreja.GestaoQuadrangular.application.dto.MembroResponseDTO;
import com.igreja.GestaoQuadrangular.application.dto.RelatorioSemanalDTO;
import com.igreja.GestaoQuadrangular.domain.entity.*;
import com.igreja.GestaoQuadrangular.domain.repository.*;
import com.igreja.GestaoQuadrangular.num.StatusEspiritual;
import com.igreja.GestaoQuadrangular.num.TipoReuniao;
import com.igreja.GestaoQuadrangular.web.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class LiderService {

    private final LiderRepository liderRepository;
    private final CelulaRepository celulaRepository;
    private final MembroRepository membroRepository;
    private final UsuarioRepository usuarioRepository;
    private final PresencaRepository presencaRepository;

    public LiderService(LiderRepository liderRepository,
                        CelulaRepository celulaRepository,
                        MembroRepository membroRepository,
                        UsuarioRepository usuarioRepository,
                        PresencaRepository presencaRepository) {
        this.liderRepository = liderRepository;
        this.celulaRepository = celulaRepository;
        this.membroRepository = membroRepository;
        this.usuarioRepository = usuarioRepository;
        this.presencaRepository = presencaRepository;
    }
    private Celula getMinhaCelula() {
        Lider lider = getLiderLogado();

        if (lider.getCelulas().isEmpty()) {
            throw new ResourceNotFoundException("Você não tem nenhuma célula associada");
        }

        if (lider.getCelulas().size() > 1) {
            throw new IllegalStateException("Líder não pode ter mais de uma célula (regra do sistema)");
        }

        // Pega o único elemento do Set
        return lider.getCelulas().iterator().next();
    }
    // =================================== AUTENTICAÇÃO ===================================

    private Lider getLiderLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + email));

        return liderRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Líder não encontrado para o usuário logado"));
    }



    // =================================== DASHBOARD ===================================

    public Map<String, Object> dashboardMinhaCelula() {
        Celula celula = getMinhaCelula();

        long totalMembros = membroRepository.countMembrosAtivosByCelula(celula.getId());
        long verdes = membroRepository.countByCelulaAndStatus(celula, StatusEspiritual.VERDE);
        long amarelos = membroRepository.countByCelulaAndStatus(celula, StatusEspiritual.AMARELO);
        long vermelhos = membroRepository.countByCelulaAndStatus(celula, StatusEspiritual.VERMELHO);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("nomeCelula", celula.getNome());
        dashboard.put("endereco", celula.getEndereco());
        dashboard.put("diaHorario", celula.getDiaSemana() + " - " + celula.getHorario());
        dashboard.put("totalMembros", totalMembros);
        dashboard.put("statusMembros", Map.of(
                "VERDE", verdes,
                "AMARELO", amarelos,
                "VERMELHO", vermelhos
        ));

        return dashboard;
    }

    // =================================== MEMBROS ===================================

    public List<MembroResponseDTO> listarMembrosMinhaCelula() {
        Celula celula = getMinhaCelula();
        List<Membro> membros = membroRepository.findByCelulaId(celula.getId());

        return membros.stream()
                .map(membro -> membroRepository.findMembroDtoById(membro.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Erro ao carregar DTO do membro " + membro.getId())))
                .toList();
    }

    @Transactional
    public Membro adicionarMembro(Long membroId) {
        Celula celula = getMinhaCelula();
        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));

        if (membro.getCelula() != null && !membro.getCelula().equals(celula)) {
            throw new IllegalArgumentException("Membro já está em outra célula");
        }

        membro.setCelula(celula);
        return membroRepository.save(membro);
    }

    @Transactional
    public Membro atualizarStatusMembro(Long membroId, StatusEspiritual novoStatus) {
        Celula celula = getMinhaCelula();
        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));

        if (!celula.equals(membro.getCelula())) {
            throw new IllegalArgumentException("Membro não pertence à sua célula");
        }

        membro.setStatus(novoStatus);
        return membroRepository.save(membro);
    }

    @Transactional
    public Membro arquivarMembro(Long membroId, boolean arquivar) {
        Celula celula = getMinhaCelula();
        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));

        if (!celula.equals(membro.getCelula())) {
            throw new IllegalArgumentException("Membro não pertence à sua célula");
        }

        membro.setArquivado(arquivar);
        return membroRepository.save(membro);
    }

    // =================================== PRESENÇA NA CÉLULA ===================================

    /**
     * Registra a presença dos membros na reunião da célula do líder logado
     */
    @Transactional
    public void registrarPresencaCelula(LocalDate data, List<Long> presentesIds) {
        if (presentesIds == null || presentesIds.isEmpty()) {
            return;
        }

        Celula celula = getMinhaCelula();

        List<Membro> membrosPresentes = membroRepository.findAllById(presentesIds);

        for (Membro membro : membrosPresentes) {
            // Validação: o membro deve pertencer à célula do líder
            if (!celula.equals(membro.getCelula())) {
                throw new IllegalArgumentException("Membro ID " + membro.getId() + " não pertence à sua célula");
            }

            // Evita duplicidade na mesma data
            boolean jaRegistrado = presencaRepository
                    .existsByMembroIdAndTipoReuniaoAndData(membro.getId(), TipoReuniao.CELULA, data);

            if (!jaRegistrado) {
                Presenca presenca = new Presenca();
                presenca.setMembro(membro);
                presenca.setTipoReuniao(TipoReuniao.CELULA);
                presenca.setData(data);
                presenca.setPresente(true);

                presencaRepository.save(presenca);
            }

        }

    }
}