package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.entity.PresencaCelula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PresencaCelulaRepository extends JpaRepository<PresencaCelula, Long> {

    Optional<PresencaCelula> findByCelulaAndData(Celula celula, LocalDate data);
}
