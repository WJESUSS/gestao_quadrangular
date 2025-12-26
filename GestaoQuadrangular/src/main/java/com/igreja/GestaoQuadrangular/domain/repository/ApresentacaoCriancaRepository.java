package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.domain.entity.ApresentacaoCrianca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ApresentacaoCriancaRepository extends JpaRepository<ApresentacaoCrianca, Long> {

    // Próximas apresentações (próximos 60 dias) - para dashboard
    List<ApresentacaoCrianca> findByDataApresentacaoBetween(LocalDate inicio, LocalDate fim);

    // Apresentações ordenadas por data (mais recentes primeiro)
    List<ApresentacaoCrianca> findAllByOrderByDataApresentacaoDesc();

    // Apresentações de um pai ou mãe específico
    List<ApresentacaoCrianca> findByPaiIdOrMaeId(Long paiId, Long maeId);
}