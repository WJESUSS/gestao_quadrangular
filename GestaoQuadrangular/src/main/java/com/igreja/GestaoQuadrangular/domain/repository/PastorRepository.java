package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.domain.entity.Pastor;
import com.igreja.GestaoQuadrangular.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface PastorRepository extends JpaRepository<Pastor, Long> {

    /**
     * Busca o pastor principal (aquele com pastorPrincipal = true)
     */
    Optional<Pastor> findByPastorPrincipalTrue();

    /**
     * Alternativa com @Query explícita (mesmo efeito)
     */
    @Query("SELECT p FROM Pastor p WHERE p.pastorPrincipal = true")
    Optional<Pastor> findPastorPrincipal();

    /**
     * Busca pastor pelo email do usuário associado
     */
    @Query("SELECT p FROM Pastor p WHERE p.usuario.email = :email")
    Optional<Pastor> findByUsuarioEmail(String email);

    /**
     * Busca todos os pastores ordenados por nome (ou ID)
     */
    @Query("SELECT p FROM Pastor p ORDER BY p.usuario.nome")
    List<Pastor> findAllOrderedByNome();

    // Em PastorRepository.java
    Optional<Pastor> findByUsuario(Usuario usuario);
}