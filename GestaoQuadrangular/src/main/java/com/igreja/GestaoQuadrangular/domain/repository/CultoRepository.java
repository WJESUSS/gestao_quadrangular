package com.igreja.GestaoQuadrangular.domain.repository;



import com.igreja.GestaoQuadrangular.domain.entity.Culto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CultoRepository extends JpaRepository<Culto, Long> {

    // Buscar culto pela data (evita cultos duplicados no mesmo dia)
    Optional<Culto> findByData(LocalDate data);

    // Verifica se já existe culto na data
    boolean existsByData(LocalDate data);
}
