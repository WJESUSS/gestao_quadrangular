package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.domain.entity.Casamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CasamentoRepository extends JpaRepository<Casamento, Long> {

    // Próximos casamentos em um intervalo de datas (ex: próximos 60 dias)
    List<Casamento> findByDataCasamentoBetween(LocalDate inicio, LocalDate fim);

    // Casamentos ordenados por data (mais próximos primeiro)
    List<Casamento> findAllByOrderByDataCasamentoAsc();

    // Casamentos futuros (após hoje)
    List<Casamento> findByDataCasamentoAfter(LocalDate hoje);

    // Casamentos de um membro específico (noivo ou noiva)
    List<Casamento> findByNoivoIdOrNoivaId(Long noivoId, Long noivaId);

    // Contagem de casamentos em um ano (para relatórios)

}