package com.igreja.GestaoQuadrangular.domain.repository;


import com.igreja.GestaoQuadrangular.application.dto.CelulaListDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.entity.Lider;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Repository
public interface CelulaRepository extends JpaRepository<Celula, Long> {
    boolean existsByNome(String nome);

    boolean existsByLider(Lider lider);

    @Query("SELECT c.nome, SIZE(c.membros) " +
            "FROM Celula c " +
            "ORDER BY SIZE(c.membros) DESC")
    List<Object[]> findTopCelulasPorTamanhoRaw();

    @Query("SELECT c FROM Celula c ORDER BY SIZE(c.membros) DESC")
    List<Celula> findTopCelulasPorNumeroMembros(Pageable pageable);

    //List<Map<String, Object>> findTopCelulasSaudaveis(int limit);
    @Query("SELECT MONTH(c.dataCriacao) as mes, YEAR(c.dataCriacao) as ano, COUNT(c) as total " +
            "FROM Celula c " +
            "WHERE c.dataCriacao IS NOT NULL " +
            "GROUP BY YEAR(c.dataCriacao), MONTH(c.dataCriacao) " +
            "ORDER BY YEAR(c.dataCriacao), MONTH(c.dataCriacao)")
    List<Object[]> contarCelulasPorMes();

    @Query("SELECT c.nome, SIZE(c.membros), l.nome " +
            "FROM Celula c " +
            "LEFT JOIN c.lider l " +
            "ORDER BY SIZE(c.membros) DESC")
    List<Object[]> findTopCelulasPorTamanhoRaw(Pageable pageable);

    @Query("SELECT new com.igreja.GestaoQuadrangular.application.dto.CelulaListDTO(" +
            "c.id, c.nome, c.diaSemana, c.horario, c.endereco, " +
            "l.nome, l.email, " +
            "SIZE(c.membros), c.dataCriacao) " +
            "FROM Celula c LEFT JOIN c.lider l " +
            "ORDER BY c.nome")
    List<CelulaListDTO> listarTodasComResumo();

    @Query("""
    SELECT c
    FROM Celula c
    LEFT JOIN FETCH c.lider
    WHERE c.id = :id
""")
    Celula buscarComLider(Long id);

}

