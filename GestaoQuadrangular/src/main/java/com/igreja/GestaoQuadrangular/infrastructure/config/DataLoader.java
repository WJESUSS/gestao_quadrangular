package com.igreja.GestaoQuadrangular.infrastructure.config;

import com.igreja.GestaoQuadrangular.domain.entity.Usuario;
import com.igreja.GestaoQuadrangular.domain.repository.UsuarioRepository;
import com.igreja.GestaoQuadrangular.num.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Cria o SUPER ADMIN se não existir
        if (usuarioRepository.findByEmail("admin@igreja.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador do Sistema");
            admin.setEmail("admin@igreja.com");
            admin.setSenha(passwordEncoder.encode("admin123")); // senha forte
            admin.setTelefone("(11) 99999-0000");
            admin.setRole(Role.ROLE_ADMIN); // ← SUPER ADMIN
            admin.setAtivo(true);

            usuarioRepository.save(admin);

            System.out.println("========================================");
            System.out.println("SUPER ADMIN CRIADO COM SUCESSO!");
            System.out.println("Email: admin@igreja.com");
            System.out.println("Senha: admin123");
            System.out.println("Role: ROLE_ADMIN → acesso total ao sistema");
            System.out.println("Use este usuário para criar o primeiro pastor.");
            System.out.println("========================================");
        }

        // Opcional: cria o pastor principal automaticamente também
        if (usuarioRepository.findByEmail("pastor@igreja.com").isEmpty()) {
            Usuario pastor = new Usuario();
            pastor.setNome("Pastor Principal");
            pastor.setEmail("pastor@igreja.com");
            pastor.setSenha(passwordEncoder.encode("pastor123"));
            pastor.setTelefone("(11) 99999-1111");
            pastor.setRole(Role.ROLE_PASTOR);
            pastor.setAtivo(true);

            usuarioRepository.save(pastor);

            System.out.println("========================================");
            System.out.println("PASTOR PRINCIPAL CRIADO AUTOMATICAMENTE!");
            System.out.println("Email: pastor@igreja.com");
            System.out.println("Senha: pastor123");
            System.out.println("========================================");
        }
    }
}