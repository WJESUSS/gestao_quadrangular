package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.entity.MensagemChat;
import com.igreja.GestaoQuadrangular.domain.entity.MetaCelula;
import com.igreja.GestaoQuadrangular.domain.entity.RelatorioSemanal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RelatorioSemanalRepository extends JpaRepository<RelatorioSemanal, Long> {
    List<RelatorioSemanal> findByCelulaOrderByDataRelatorioDesc(Celula celula, Pageable pageable);
    @Query("SELECT AVG(r.visitantes) FROM RelatorioSemanal r WHERE r.celula = :celula AND r.dataRelatorio >= :data")
    Double mediaVisitantesDesde(@Param("celula") Celula celula, @Param("data") LocalDate data);
}

