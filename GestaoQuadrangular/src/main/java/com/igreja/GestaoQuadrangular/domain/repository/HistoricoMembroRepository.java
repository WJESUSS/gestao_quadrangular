package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.domain.entity.HistoricoMembro;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoricoMembroRepository extends JpaRepository<HistoricoMembro, Long> {
    List<HistoricoMembro> findByMembroIdOrderByDataEventoDesc(Long membroId);
}
