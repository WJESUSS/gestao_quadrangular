package com.igreja.GestaoQuadrangular.servicce;

import com.igreja.GestaoQuadrangular.domain.entity.*;
import com.igreja.GestaoQuadrangular.domain.repository.*;
import com.igreja.GestaoQuadrangular.num.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final LiderRepository liderRepository;
    private final PastorRepository pastorRepository;
    private final SecretarioRepository secretarioRepository;
    private final TesoureiroRepository tesoureiroRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            LiderRepository liderRepository,
            PastorRepository pastorRepository,
            SecretarioRepository secretarioRepository,
            TesoureiroRepository tesoureiroRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.liderRepository = liderRepository;
        this.pastorRepository = pastorRepository;
        this.secretarioRepository = secretarioRepository;
        this.tesoureiroRepository = tesoureiroRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    // ================= LOGIN =================
    public String login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }

        Instant agora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("gestao-quadrangular")
                .subject(usuario.getEmail())
                .issuedAt(agora)
                .expiresAt(agora.plus(1, ChronoUnit.DAYS))
                .claim("role", usuario.getRole().name())
                .build();

        JwsHeader header = JwsHeader.with(() -> "HS256").build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    // ================= CADASTRO =================
    public Usuario cadastrarUsuario(Usuario usuarioNovo, Usuario usuarioLogado) {

        if (usuarioLogado == null) {
            throw new AccessDeniedException("Você precisa estar logado para cadastrar usuários.");
        }

        // ===== VERIFICA EMAIL DUPLICADO =====
        if (usuarioRepository.findByEmail(usuarioNovo.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado: " + usuarioNovo.getEmail());
        }

        Role roleNovo = usuarioNovo.getRole();
        Role roleLogado = usuarioLogado.getRole();

        // ===== REGRAS DE PERMISSÃO =====
        switch (roleLogado) {
            case ROLE_PASTOR:
                // Pastor pode criar todos os tipos
                break;

            case ROLE_SECRETARIO:
                if (!(roleNovo == Role.ROLE_LIDER || roleNovo == Role.ROLE_MEMBRO)) {
                    throw new AccessDeniedException("Secretário só pode criar Líder ou Membro.");
                }
                break;

            case ROLE_LIDER:
                if (roleNovo != Role.ROLE_MEMBRO) {
                    throw new AccessDeniedException("Líder só pode adicionar Membros à sua célula.");
                }
                // Membro deve ficar na célula do líder
                usuarioNovo.setCelula(usuarioLogado.getCelula());
                break;

            case ROLE_TESOUREIRO:
                throw new AccessDeniedException("Tesoureiro não pode criar usuários.");

            default:
                throw new AccessDeniedException("Função não autorizada para criar usuários.");
        }

        // ===== Criptografia de senha =====
        usuarioNovo.setSenha(passwordEncoder.encode(usuarioNovo.getSenha()));
        usuarioNovo.setAtivo(true);

        // ===== Salva usuário =====
        Usuario usuarioSalvo = usuarioRepository.save(usuarioNovo);

        // ===== Cria entidade específica dependendo do role =====
        switch (roleNovo) {
            case ROLE_LIDER:
                Lider lider = new Lider();
                lider.setUsuario(usuarioSalvo);
                lider.setNome(usuarioSalvo.getNome());
                lider.setEmail(usuarioSalvo.getEmail());
                liderRepository.save(lider);
                break;

            case ROLE_PASTOR:
                Pastor pastor = new Pastor();
                pastor.setUsuario(usuarioSalvo);
                pastorRepository.save(pastor);
                break;

            case ROLE_SECRETARIO:
                Secretario secretario = new Secretario();
                secretario.setUsuario(usuarioSalvo);
                secretarioRepository.save(secretario);
                break;

            case ROLE_TESOUREIRO:
                Tesoureiro tesoureiro = new Tesoureiro();
                tesoureiro.setUsuario(usuarioSalvo);
                tesoureiroRepository.save(tesoureiro);
                break;

            default:
                // ROLE_MEMBRO não precisa de entidade extra
                break;
        }

        return usuarioSalvo;
    }
    // Listar todos usuários
    public List<Usuario> getTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    // Atualizar usuário
    public Usuario atualizarUsuario(Long id, Usuario atualizado) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        existente.setNome(atualizado.getNome());
        existente.setEmail(atualizado.getEmail());
        // Atualizar outras propriedades necessárias
        return usuarioRepository.save(existente);
    }

    // Deletar usuário
 // Você precisará injetar o repositório de líderes

    @Transactional // Importante para garantir que as duas exclusões ocorram juntas
    public void deletarUsuario(Long id) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 1. Removemos o registro na tabela de líderes que aponta para este usuário
        // O campo no banco é membro_id, então buscamos por esse vínculo
        liderRepository.deleteByUsuario(existente);

        // 2. Agora o usuário está "livre" para ser deletado
        usuarioRepository.delete(existente);
    }

}
