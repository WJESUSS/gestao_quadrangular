package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.domain.entity.Presenca;
import com.igreja.GestaoQuadrangular.num.TipoReuniao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PresencaRepository extends JpaRepository<Presenca, Long> {

    // Todas as presenças de um membro
    List<Presenca> findByMembroId(Long membroId);

    // Verifica se já existe presença registrada para evitar duplicidade
    boolean existsByMembroIdAndTipoReuniaoAndData(
            Long membroId,
            TipoReuniao tipoReuniao,
            LocalDate data);


    // Busca presenças de um membro em um período específico e tipo de reunião
    List<Presenca> findByMembroIdAndTipoReuniaoAndDataBetween(
            Long membroId,
            TipoReuniao tipoReuniao,
            LocalDate inicio,
            LocalDate fim);

    // Busca todas as presenças de um tipo de reunião em uma data específica (relatórios)
    @Query("SELECT p FROM Presenca p JOIN FETCH p.membro WHERE p.tipoReuniao = :tipoReuniao AND p.data = :data")
    List<Presenca> findByTipoReuniaoAndData(@Param("tipoReuniao") TipoReuniao tipoReuniao, @Param("data") LocalDate data);

    // Opcional: todas as presenças de um membro em um tipo de reunião (sem período)
    List<Presenca> findByMembroIdAndTipoReuniao(Long membroId, TipoReuniao tipoReuniao);
    List<Presenca> findByMembroIdAndTipoReuniaoInAndDataBetween(
            Long membroId,
            List<TipoReuniao> tipoReuniao,   // Note que é List<TipoReuniao>, não apenas TipoReuniao
            LocalDate dataInicio,
            LocalDate dataFim
    );
}