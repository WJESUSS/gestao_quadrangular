package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.domain.entity.TurmaEscolaBiblica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TurmaEscolaBiblicaRepository extends JpaRepository<TurmaEscolaBiblica, Long> {
    List<TurmaEscolaBiblica> findByTipoTurma(TurmaEscolaBiblica.TipoTurma tipo);
    List<TurmaEscolaBiblica> findByDataFimAfter(java.time.LocalDate data);


  // se precisar para listagem
}