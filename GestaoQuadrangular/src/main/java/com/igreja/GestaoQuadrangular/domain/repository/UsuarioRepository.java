package com.igreja.GestaoQuadrangular.domain.repository;

import com.igreja.GestaoQuadrangular.domain.entity.Usuario;
import com.igreja.GestaoQuadrangular.num.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
    List<Usuario> findByCelulaIsNullAndRole(Role role);
}
