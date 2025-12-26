package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.domain.entity.TransferenciaMembresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransferenciaRepository extends JpaRepository<TransferenciaMembresia, Long> {

    // Busca por ID (já vem do JpaRepository, mas reforçando)
    Optional<TransferenciaMembresia> findById(Long id);

    // Todas as transferências de um membro específico
    List<TransferenciaMembresia> findByMembroIdOrderByDataTransferenciaDesc(Long membroId);

    // Transferências de entrada (recebidas da outra igreja)
    List<TransferenciaMembresia> findByTipoOrderByDataTransferenciaDesc(String tipo); // tipo = "ENTRADA"

    // Transferências de saída (enviadas para outra igreja)
    List<TransferenciaMembresia> findByTipoAndDataTransferenciaAfter(String tipo, LocalDate data);

    // Transferências em um período específico
    List<TransferenciaMembresia> findByDataTransferenciaBetween(LocalDate inicio, LocalDate fim);

    // Transferências ordenadas por data (mais recentes primeiro)
    List<TransferenciaMembresia> findAllByOrderByDataTransferenciaDesc();

    // Contagem de transferências por tipo (útil para dashboard/relatórios)
    long countByTipo(String tipo);

    // Contagem de transferências no último ano (exemplo para estatísticas)
    @Query("SELECT COUNT(t) FROM TransferenciaMembresia t WHERE t.dataTransferencia >= :dataInicio")
    long countByDataTransferenciaAfter(@Param("dataInicio") LocalDate dataInicio);

    // Busca a última transferência de um membro (mais recente)
    Optional<TransferenciaMembresia> findFirstByMembroIdOrderByDataTransferenciaDesc(Long membroId);

    // Transferências pendentes ou com motivo específico (ex: "PEDIDO")
    List<TransferenciaMembresia> findByMotivoContainingIgnoreCase(String palavraChave);
}