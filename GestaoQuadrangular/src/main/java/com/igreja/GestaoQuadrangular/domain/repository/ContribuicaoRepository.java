package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.domain.entity.Contribuicao;
import com.igreja.GestaoQuadrangular.num.TipoOferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ContribuicaoRepository extends JpaRepository<Contribuicao, Long> {

    List<Contribuicao> findByMembroId(Long membroId);

    List<Contribuicao> findByDataBetween(LocalDate inicio, LocalDate fim);

    // Total por tipo de oferta em um período
    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM Contribuicao c " +
            "WHERE c.tipoOferta = :tipo AND c.data BETWEEN :inicio AND :fim")
    BigDecimal sumByTipoOfertaAndDataBetween(@Param("tipo") TipoOferta tipo,
                                             @Param("inicio") LocalDate inicio,
                                             @Param("fim") LocalDate fim);

    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM Contribuicao c " +
            "JOIN c.membro m JOIN m.celula cel " +
            "WHERE cel.id = :celulaId AND c.data BETWEEN :inicio AND :fim")
    BigDecimal sumByCelulaIdAndDataBetween(@Param("celulaId") Long celulaId,
                                           @Param("inicio") LocalDate inicio,
                                           @Param("fim") LocalDate fim);
    // Contribuições de um membro em um ano (para fiel/infiél)
    List<Contribuicao> findByMembroIdAndDataBetween(Long membroId, LocalDate inicioAno, LocalDate fimAno);
}