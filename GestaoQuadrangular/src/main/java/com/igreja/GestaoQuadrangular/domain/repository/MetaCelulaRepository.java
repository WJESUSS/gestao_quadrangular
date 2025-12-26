package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.entity.MetaCelula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MetaCelulaRepository extends JpaRepository<MetaCelula, Long> {
    Optional<MetaCelula> findByCelulaAndAtivaTrue(Celula celula);
}
