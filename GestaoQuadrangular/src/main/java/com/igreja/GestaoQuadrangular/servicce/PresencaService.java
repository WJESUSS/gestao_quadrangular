package com.igreja.GestaoQuadrangular.servicce;

import com.igreja.GestaoQuadrangular.application.dto.PresencaResponseDTO;
import com.igreja.GestaoQuadrangular.domain.entity.*;
import com.igreja.GestaoQuadrangular.domain.repository.CultoRepository;
import com.igreja.GestaoQuadrangular.domain.repository.MembroRepository;
import com.igreja.GestaoQuadrangular.domain.repository.PresencaCelulaRepository;
import com.igreja.GestaoQuadrangular.domain.repository.PresencaRepository;
import com.igreja.GestaoQuadrangular.num.TipoReuniao;
import com.igreja.GestaoQuadrangular.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PresencaService {

    private final PresencaRepository presencaRepository;

    private final PresencaCelulaRepository presencaCelulaRepository;
    private final MembroRepository membroRepository;
    private final CultoRepository cultoRepository;

    public PresencaService(
            PresencaRepository presencaRepository, PresencaCelulaRepository presencaCelulaRepository,
            MembroRepository membroRepository,
            CultoRepository cultoRepository) {
        this.presencaRepository = presencaRepository;
        this.presencaCelulaRepository = presencaCelulaRepository;
        this.membroRepository = membroRepository;
        this.cultoRepository = cultoRepository;
    }

    @Transactional
    public Presenca registrarPresenca(Long membroId, Long cultoId, boolean presente) {
        // Busca e valida o membro
        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro com ID " + membroId + " não encontrado"));

        // Busca e valida o culto
        Culto culto = cultoRepository.findById(cultoId)
                .orElseThrow(() -> new ResourceNotFoundException("Culto com ID " + cultoId + " não encontrado"));

        // Cria a presença
        Presenca presenca = new Presenca();
        presenca.setMembro(membro);
        presenca.setPresente(presente);

        // Atualiza faltas consecutivas
        int novasFaltas = presente ? 0 : membro.getFaltasConsecutivas() + 1;
        membro.setFaltasConsecutivas(novasFaltas);
        membroRepository.save(membro);  // Atualiza o membro diretamente (mais eficiente)

        // Salva e retorna a presença
        return presencaRepository.save(presenca);
    }
    @Transactional
    public void registrarPresencaDiscipulado(LocalDate data, List<Long> presentesIds) {
        registrarPresencaGenerica(TipoReuniao.DISCIPULADO, data, presentesIds);
    }
    @Transactional
    protected void registrarPresencaGenerica(TipoReuniao tipoReuniao, LocalDate data, List<Long> presentesIds) {
        if (presentesIds == null || presentesIds.isEmpty()) {
            return;
        }

        List<Membro> membros = membroRepository.findAllById(presentesIds);

        for (Membro membro : membros) {
            boolean jaRegistrado = presencaRepository
                    .existsByMembroIdAndTipoReuniaoAndData(membro.getId(), tipoReuniao, data);

            if (!jaRegistrado) {
                Presenca presenca = new Presenca();
                presenca.setMembro(membro);
                presenca.setTipoReuniao(tipoReuniao);
                presenca.setData(data);
                presenca.setPresente(true);

                presencaRepository.save(presenca);
            }
        }
    }

    @Transactional
    public void registrarPresencaCulto(TipoReuniao tipoReuniao, LocalDate data, List<Long> presentesIds) {
        // Validação básica
        if (tipoReuniao == null) {
            throw new IllegalArgumentException("O tipo de reunião é obrigatório");
        }
        if (data == null) {
            throw new IllegalArgumentException("A data é obrigatória");
        }
        if (presentesIds == null || presentesIds.isEmpty()) {
            // Nada a registrar
            return;
        }

        // Valida se o tipo é realmente um culto (evita erro de uso)
        if (!isTipoCulto(tipoReuniao)) {
            throw new IllegalArgumentException("Tipo de reunião inválido para culto: " + tipoReuniao);
        }

        // Busca todos os membros informados
        List<Membro> membrosPresentes = membroRepository.findAllById(presentesIds);

        if (membrosPresentes.isEmpty()) {
            return;
        }

        // Registra a presença para cada membro
        for (Membro membro : membrosPresentes) {
            // Verifica se já foi registrada presença para evitar duplicidade
            boolean jaRegistrado = presencaRepository
                    .existsByMembroIdAndTipoReuniaoAndData(membro.getId(), tipoReuniao, data);

            if (!jaRegistrado) {
                Presenca presenca = new Presenca();
                presenca.setMembro(membro);
                presenca.setTipoReuniao(tipoReuniao);
                presenca.setData(data);
                presenca.setPresente(true);

                presencaRepository.save(presenca);
            }
        }
    }

    private boolean isTipoCulto(TipoReuniao tipoReuniao) {
        if (tipoReuniao == null) {
            return false;
        }

        return switch (tipoReuniao) {
            case CULTO_QUARTA,
                 CULTO_QUINTA,
                 CULTO_DOMINGO_MANHA,
                 CULTO_DOMINGO_NOITE -> true;
            default -> false;
        };
    }
    @Transactional(readOnly = true)
    public List<PresencaResponseDTO> listarPresencasPorTipoEData(TipoReuniao tipoReuniao, LocalDate data) {
        List<Presenca> presencas = presencaRepository.findByTipoReuniaoAndData(tipoReuniao, data);

        return presencas.stream()
                .map(p -> {
                    Membro membro = p.getMembro();
                    // Pega o ID da célula com segurança (evita NullPointerException)
                    Long idDaCelula = (membro.getCelula() != null) ? membro.getCelula().getId() : null;

                    return new PresencaResponseDTO(
                            membro.getId(),
                            idDaCelula,      // AGORA PASSAMOS O ID PARA O FRONTEND
                            membro.getNome(),
                            membro.getTelefone(),
                            p.isPresente()
                    );
                })
                .sorted((a, b) -> a.nomeMembro().compareToIgnoreCase(b.nomeMembro()))
                .toList();
    }
    @Transactional
    public void registrarPresencaCelula(LocalDate data, List<Long> presentesIds, Celula celula) {

        if (celula == null) {
            throw new IllegalArgumentException("Célula não pode ser nula");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data da reunião não pode ser nula");
        }
        if (presentesIds == null) {
            presentesIds = List.of(); // evita NullPointer
        }

        // 1. Busca todos os membros ativos da célula
        List<Membro> membrosDaCelula = membroRepository
                .findByCelulaIdAndArquivadoFalse(celula.getId());

        if (membrosDaCelula.isEmpty()) {
            // Pode ser uma célula nova ou sem membros — nada a fazer
            return;
        }

        // 2. Cria ou atualiza o registro de presença para esta célula e data
        PresencaCelula presencaCelula = presencaCelulaRepository
                .findByCelulaAndData(celula, data)
                .orElse(new PresencaCelula());

        presencaCelula.setCelula(celula);
        presencaCelula.setData(data);
        presencaCelula.getPresentes().clear(); // limpa os antigos, se existir

        // 3. Marca os membros presentes
        for (Membro membro : membrosDaCelula) {
            if (presentesIds.contains(membro.getId())) {
                presencaCelula.getPresentes().add(membro);
                // Opcional: resetar faltas consecutivas se você tiver essa lógica
                // membro.setFaltasConsecutivas(0);
            } else {
                // Membro ausente — você pode registrar falta aqui se quiser
                // ex: membroService.registrarFalta(membro.getId());
            }
        }

        // 4. Salva o registro de presença da célula
        presencaCelulaRepository.save(presencaCelula);

        // 5. Opcional: atualizar estatísticas ou disparar eventos
        // ex: atualizar media de frequência, enviar notificações, etc.
    }
}