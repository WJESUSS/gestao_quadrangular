package com.igreja.GestaoQuadrangular.domain.repository;


import com.igreja.GestaoQuadrangular.domain.entity.Lider;
import com.igreja.GestaoQuadrangular.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LiderRepository extends JpaRepository<Lider, Long> {

    boolean existsByEmail(String email);

    // Novo método
    boolean existsByUsuarioId(Long usuarioId);

    Optional<Lider> findByUsuarioId(Long usuarioId);

    @Modifying
    @Transactional
    void deleteByUsuario(Usuario usuario);
}

