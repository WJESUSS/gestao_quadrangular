package com.igreja.GestaoQuadrangular.servicce;

import com.igreja.GestaoQuadrangular.application.dto.RelatorioDiscipuladoDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.repository.MembroRepository;
import com.igreja.GestaoQuadrangular.num.EscadaSucesso;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiscipuladoService {

    private final MembroRepository membroRepository;

    public DiscipuladoService(MembroRepository membroRepository) {
        this.membroRepository = membroRepository;
    }

    @Transactional(readOnly = true)
    public List<RelatorioDiscipuladoDTO> relatorioPorCelula(Long celulaId) {
        List<Membro> membros = membroRepository.findByCelulaIdAndArquivadoFalse(celulaId);
        return membros.stream()
                .map(m -> new RelatorioDiscipuladoDTO(
                        m.getId(),
                        m.getNome(),
                        m.getEscadaSucesso(),
                        m.getDataUltimaAtualizacaoEscada(), // assuma campo em Membro
                        m.getObservacaoDiscipulado() // assuma campo
                ))
                .toList();
    }

    // Atualizar etapa de um membro
    @Transactional
    public void atualizarEtapa(Long membroId, EscadaSucesso novaEtapa) {
        Membro membro = membroRepository.findById(membroId).orElseThrow();
        membro.setEscadaSucesso(novaEtapa);
        membro.setDataUltimaAtualizacaoEscada(LocalDate.now());
        membroRepository.save(membro);
    }
}