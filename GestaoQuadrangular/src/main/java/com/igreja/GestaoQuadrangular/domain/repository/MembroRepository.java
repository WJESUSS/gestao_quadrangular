package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.application.dto.MembroResponseDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.num.EscadaSucesso;
import com.igreja.GestaoQuadrangular.num.StatusEspiritual;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembroRepository extends JpaRepository<Membro, Long> {

    // ===================== DTO =====================
    @Query("""
        SELECT new com.igreja.GestaoQuadrangular.application.dto.MembroResponseDTO(
            m.id, m.nome, m.telefone, m.email, m.faltasConsecutivas,
            m.arquivado, m.status, c.id, c.nome
        )
        FROM Membro m
        LEFT JOIN m.celula c
        WHERE m.id = :id
    """)
    Optional<MembroResponseDTO> findMembroDtoById(@Param("id") Long id);

    @Query("""
        SELECT new com.igreja.GestaoQuadrangular.application.dto.MembroResponseDTO(
            m.id, m.nome, m.telefone, m.email, m.faltasConsecutivas,
            m.arquivado, m.status, c.id, c.nome
        )
        FROM Membro m
        LEFT JOIN m.celula c
    """)
    List<MembroResponseDTO> findAllMembroDtos();

    // ===================== Contagens =====================
    @Query("SELECT COUNT(m) FROM Membro m WHERE m.celula.id = :celulaId AND m.arquivado = false")
    long countMembrosAtivosByCelula(@Param("celulaId") Long celulaId);

    long countByArquivadoFalse();

    long countByArquivadoTrue();

    long countByStatus(StatusEspiritual status);

    long countByCelulaAndStatus(Celula celula, StatusEspiritual statusEspiritual);

    @Query("SELECT COUNT(m) FROM Membro m WHERE m.fotoUrl IS NULL OR m.fotoUrl = ''")
    long countByFotoUrlIsNullOrFotoUrlEmpty();

    @Query("SELECT COUNT(m) FROM Membro m WHERE m.email IS NULL OR m.email = '' OR m.telefone IS NULL OR m.telefone = ''")
    long countByEmailIsNullOrTelefoneIsNull();

    long countByCelulaIdAndArquivadoFalse(Long id);

    boolean existsByEmail(String email);

    // ===================== Busca =====================
    List<Membro> findByCelulaIdAndArquivadoFalse(Long celulaId);

    List<Membro> findByNomeContainingIgnoreCase(String nome);

    List<Membro> findByCelulaId(Long celulaId);

    List<Membro> findByArquivadoFalse();

    List<Membro> findByCelulaIsNull();

    Optional<Membro> findByEmail(String email);

    // ===================== PRESENÇAS DOMINGO =====================
    @Query(value = """
        SELECT COUNT(*) 
        FROM presencas p
        WHERE p.membro_id = :membroId
          AND p.tipo_reuniao IN ('CULTO_DOMINGO_MANHA', 'CULTO_DOMINGO_NOITE')
          AND EXTRACT(YEAR FROM p.data) = :ano
          AND EXTRACT(MONTH FROM p.data) = :mes
          AND p.presente = true
    """, nativeQuery = true)
    long contarPresencasDomingoNoMes(
            @Param("membroId") Long membroId,
            @Param("ano") int ano,
            @Param("mes") int mes
    );

    @Query(value = """
        SELECT COUNT(*) 
        FROM presencas p
        WHERE p.membro_id = :membroId
          AND p.tipo_reuniao IN ('CULTO_DOMINGO_MANHA', 'CULTO_DOMINGO_NOITE')
          AND EXTRACT(YEAR FROM p.data) = :ano
          AND EXTRACT(MONTH FROM p.data) = :mes
          AND p.presente = false
    """, nativeQuery = true)
    long contarFaltasDomingoNoMes(
            @Param("membroId") Long membroId,
            @Param("ano") int ano,
            @Param("mes") int mes
    );

    // ===================== ANIVERSARIANTES =====================
    @Query("SELECT m FROM Membro m WHERE EXTRACT(MONTH FROM m.dataNascimento) = :mes AND EXTRACT(DAY FROM m.dataNascimento) = :dia AND m.arquivado = false ORDER BY m.nome")
    List<Membro> findAniversariantesDoDia(@Param("mes") int mes, @Param("dia") int dia);

    @Query("SELECT m FROM Membro m WHERE EXTRACT(MONTH FROM m.dataNascimento) = :mes")
    List<Membro> findByDataNascimentoMonth(@Param("mes") int mes);

    // 🔹 Correção para PostgreSQL: TO_CHAR em vez de DATE_FORMAT
    @Query(value = """
        SELECT m FROM Membro m 
        WHERE TO_CHAR(m.dataNascimento, 'MM-DD') BETWEEN :inicio AND :fim
        AND m.arquivado = false
    """)
    List<Membro> findAniversariantesProximos7Dias(
            @Param("inicio") String inicio,
            @Param("fim") String fim
    );

    // ===================== APTOS PARA VOTAÇÃO =====================
    @Query("""
    SELECT m FROM Membro m
    WHERE m.arquivado = false
      AND (m.sobDisciplina IS NULL OR m.sobDisciplina = false)
      AND m.dataBatismo IS NOT NULL
      AND m.dataNascimento <= :dataLimiteIdade
      AND m.faltasConsecutivas <= :maxFaltas
      AND m.escadaSucesso >= :etapaMinima
    ORDER BY m.nome
""")
    List<Membro> findMembrosAptosParaVotar(
            @Param("dataLimiteIdade") LocalDate dataLimiteIdade,
            @Param("maxFaltas") int maxFaltas,
            @Param("etapaMinima") EscadaSucesso etapaMinima
    );

    @Query("""
    SELECT m FROM Membro m
    WHERE m.arquivado = false
      AND (m.sobDisciplina IS NULL OR m.sobDisciplina = false)
      AND m.dataBatismo IS NOT NULL
      AND m.dataNascimento <= :dataLimiteIdade
      AND m.faltasConsecutivas <= :maxFaltas
      AND m.escadaSucesso >= :etapaMinima
    ORDER BY m.nome
""")
    Page<Membro> findMembrosAptosParaVotar(
            @Param("dataLimiteIdade") LocalDate dataLimiteIdade,
            @Param("maxFaltas") int maxFaltas,
            @Param("etapaMinima") EscadaSucesso etapaMinima,
            Pageable pageable
    );


    // ===================== INATIVOS =====================
    @Query("""
        SELECT m FROM Membro m 
        WHERE m.arquivado = false 
          AND (m.dataUltimaPresenca IS NULL OR m.dataUltimaPresenca < :dataLimite)
        ORDER BY m.nome
    """)
    Page<Membro> findInativosPorMeses(@Param("dataLimite") LocalDate dataLimite, Pageable pageable);

    // ===================== DEFAULT METHODS (implementações opcionais) =====================
    default List<Membro> findAniversariantesDaSemana() {
        return List.of();
    }

    default List<Membro> findAniversariantesDoMes() {
        return List.of();
    }


}