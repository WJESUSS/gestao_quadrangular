package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.domain.entity.Visitante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface VisitanteRepository extends JpaRepository<Visitante, Long> {

    Optional<Visitante> findFirstByNomeIgnoreCaseAndTelefone(String nome, String telefone);

    List<Visitante> findByDataPrimeiraVisitaAfter(LocalDate data);
    List<Visitante> findByDataPrimeiraVisitaBefore(LocalDate data);
    List<Visitante> findByDataPrimeiraVisitaBetween(LocalDate inicio, LocalDate fim);

    List<Visitante> findByCelulaId(Long celulaId);
    List<Visitante> findByStatus(Visitante.StatusVisitante status);
    List<Visitante> findByNomeContainingIgnoreCaseOrTelefoneContaining(String nome, String telefone);

    @Query("SELECT COUNT(v) FROM Visitante v WHERE v.dataPrimeiraVisita >= :data")
    long countByDataPrimeiraVisitaAfter(@Param("data") LocalDate data);

    @Query("SELECT COUNT(v) FROM Visitante v")
    Long countTodosVisitantes();

    @Query("SELECT COUNT(v) FROM Visitante v WHERE v.dataPrimeiraVisita >= :umaSemanaAtras")
    Long countVisitantesRecentes(@Param("umaSemanaAtras") LocalDate umaSemanaAtras);

    // ✅ ÚNICO método de contagem por célula e período
    int countByCelulaIdAndDataPrimeiraVisitaBetween(
            Long celulaId,
            LocalDate inicio,
            LocalDate fim
    );

}
