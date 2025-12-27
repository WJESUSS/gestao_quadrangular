package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.entity.RelatorioSemanal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RelatorioSemanalRepository extends JpaRepository<RelatorioSemanal, Long> {

    // Busca paginada dos relatórios de uma célula específica (ordem descendente de data)
    List<RelatorioSemanal> findByCelulaOrderByDataRelatorioDesc(Celula celula, Pageable pageable);

    // Busca relatório específico para evitar duplicados na mesma data (muito útil!)
    Optional<RelatorioSemanal> findByCelulaIdAndDataRelatorio(Long celulaId, LocalDate data);

    // --- CONSULTAS PARA O PASTOR (CONSOLIDADOS E MÉTRICAS) ---

    /**
     * Verifica se existe pelo menos um relatório no período para uma célula
     */
    @Query("SELECT COUNT(r) > 0 FROM RelatorioSemanal r " +
            "WHERE r.celula.id = :celulaId AND r.dataRelatorio BETWEEN :inicio AND :fim")
    boolean existeRelatorioNoPeriodo(
            @Param("celulaId") Long celulaId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Total de conversões no período (todas as células)
     */
    @Query("SELECT COALESCE(SUM(r.conversoes), 0) FROM RelatorioSemanal r " +
            "WHERE r.dataRelatorio BETWEEN :inicio AND :fim")
    Integer somarConversoesNoPeriodo(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Total de batismos no período (todas as células)
     */
    @Query("SELECT COALESCE(SUM(r.batismos), 0) FROM RelatorioSemanal r " +
            "WHERE r.dataRelatorio BETWEEN :inicio AND :fim")
    Integer somarBatismosNoPeriodo(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Média de presentes no período (todas as células)
     */
    @Query("SELECT COALESCE(AVG(r.presentes), 0.0) FROM RelatorioSemanal r " +
            "WHERE r.dataRelatorio BETWEEN :inicio AND :fim")
    Double mediaPresentesNoPeriodo(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Média de visitantes no período (todas as células)
     */
    @Query("SELECT COALESCE(AVG(r.visitantes), 0.0) FROM RelatorioSemanal r " +
            "WHERE r.dataRelatorio BETWEEN :inicio AND :fim")
    Double mediaVisitantesNoPeriodo(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Média de visitantes de uma célula específica desde uma data
     */
    @Query("SELECT COALESCE(AVG(r.visitantes), 0.0) FROM RelatorioSemanal r " +
            "WHERE r.celula = :celula AND r.dataRelatorio >= :data")
    Double mediaVisitantesDesde(
            @Param("celula") Celula celula,
            @Param("data") LocalDate data);

    // --- MÉTRICAS GLOBAIS (TODOS OS TEMPOS) ---

    @Query("SELECT COALESCE(SUM(r.conversoes), 0) FROM RelatorioSemanal r")
    Integer somarTodasConversoes();

    @Query("SELECT COALESCE(SUM(r.batismos), 0) FROM RelatorioSemanal r")
    Integer somarTodosBatismos();

    @Query("SELECT COALESCE(SUM(r.presentes), 0) FROM RelatorioSemanal r")
    Integer somarTodosPresentes();

    @Query("SELECT COALESCE(SUM(r.visitantes), 0) FROM RelatorioSemanal r")
    Integer somarTodosVisitantes();

    // --- NOVAS CONSULTAS ÚTEIS PARA O PASTOR ---

    /**
     * Lista todas as células que ENVIARAM relatório no período (para identificar ausentes)
     */
    @Query("SELECT DISTINCT r.celula FROM RelatorioSemanal r " +
            "WHERE r.dataRelatorio BETWEEN :inicio AND :fim")
    List<Celula> findCelulasComRelatorioNoPeriodo(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Conta quantas células enviaram pelo menos um relatório no período
     */
    @Query("SELECT COUNT(DISTINCT r.celula) FROM RelatorioSemanal r " +
            "WHERE r.dataRelatorio BETWEEN :inicio AND :fim")
    long countCelulasComRelatorioNoPeriodo(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);


    @Query("""

            SELECT SUM(r.visitantes) 
   FROM RelatorioSemanal r 
   WHERE r.celula.id = :celulaId 
     AND r.dataRelatorio BETWEEN :inicio AND :fim
   """)
    Integer somarVisitantesPorCelulaNoPeriodo(@Param("celulaId") Long celulaId,
                                              @Param("inicio") LocalDate inicio,
                                              @Param("fim") LocalDate fim);
}