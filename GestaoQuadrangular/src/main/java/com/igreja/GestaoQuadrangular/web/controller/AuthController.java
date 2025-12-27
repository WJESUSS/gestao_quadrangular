package com.igreja.GestaoQuadrangular.web.controller;

import com.igreja.GestaoQuadrangular.application.dto.UsuarioRequestDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Usuario;
import com.igreja.GestaoQuadrangular.domain.repository.UsuarioRepository;
import com.igreja.GestaoQuadrangular.servicce.AuthService;
import com.igreja.GestaoQuadrangular.application.dto.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
 private final UsuarioRepository usuarioRepository;
    private final AuthService authService;

    public AuthController(UsuarioRepository usuarioRepository, AuthService authService) {
        this.usuarioRepository = usuarioRepository;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getSenha());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody @Valid UsuarioRequestDTO dto) {
        Usuario usuarioLogado = usuarioRepository.findByEmail("pastor@igreja.com")
                .orElseThrow(() -> new RuntimeException("Pastor não encontrado"));

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.getNome());
        novoUsuario.setSobrenome(dto.getSobrenome());
        novoUsuario.setTitulo(dto.getTitulo());
        novoUsuario.setEmail(dto.getEmail());
        novoUsuario.setSenha(dto.getSenha());
        novoUsuario.setRole(dto.getRole());
        novoUsuario.setTelefone(dto.getTelefone());

        Usuario cadastrado = authService.cadastrarUsuario(novoUsuario, usuarioLogado);

        return ResponseEntity.ok(Map.of(
                "id", cadastrado.getId(),
                "nome", cadastrado.getNomeCompleto(),
                "email", cadastrado.getEmail(),
                "role", cadastrado.getRole().name(),
                "ativo", cadastrado.isAtivo()
        ));
    }

    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(authService.getTodosUsuarios());
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario atualizado) {
        Usuario usuario = authService.atualizarUsuario(id, atualizado);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> deletarUsuario(@PathVariable Long id) {
        authService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/pastor/meu-perfil")
    public ResponseEntity<?> meuPerfil(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return ResponseEntity.ok(usuario);
    }

}
