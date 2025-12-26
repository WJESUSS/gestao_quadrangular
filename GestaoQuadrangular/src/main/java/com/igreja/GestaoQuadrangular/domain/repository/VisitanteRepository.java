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

    // Mantenha APENAS estes métodos (com o nome correto do campo)
    List<Visitante> findByDataPrimeiraVisitaAfter(LocalDate data);
    List<Visitante> findByDataPrimeiraVisitaBefore(LocalDate data);
    List<Visitante> findByDataPrimeiraVisitaBetween(LocalDate inicio, LocalDate fim);

    List<Visitante> findByCelulaId(Long celulaId);
    List<Visitante> findByStatus(Visitante.StatusVisitante status);
    List<Visitante> findByNomeContainingIgnoreCaseOrTelefoneContaining(String nome, String telefone);

    @Query("SELECT COUNT(v) FROM Visitante v WHERE v.dataPrimeiraVisita >= :data")
    long countByDataPrimeiraVisitaAfter(@Param("data") LocalDate data);

    // REMOVA estas duas linhas (elas causam o erro de startup):
    // List<Visitante> findByDataVisitaAfter(LocalDate trintaDiasAtras);
    // List<Visitante> findByDataVisitaBefore(LocalDate trintaDiasAtras);
}