// src/main/java/com/igreja/GestaoQuadrangular/infrastructure/security/CustomJwtAuthenticationConverter.java

package com.igreja.GestaoQuadrangular.infrastructure.security;

import com.igreja.GestaoQuadrangular.domain.entity.Usuario;
import com.igreja.GestaoQuadrangular.domain.repository.UsuarioRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    private final UsuarioRepository usuarioRepository;

    public CustomJwtAuthenticationConverter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        // Configura para usar "ROLE_" e claim "scope" ou "roles" se preferir
        this.grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        this.grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // mude se usar "scope"
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> jwtAuthorities = grantedAuthoritiesConverter.convert(jwt);

        String email = jwt.getSubject();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));

        // ← CORREÇÃO AQUI
        Collection<? extends GrantedAuthority> userAuthorities = usuario.getAuthorities();

        // Combina as authorities do JWT com as do usuário (opcional, mas recomendado)
        Set<GrantedAuthority> combinedAuthorities = new HashSet<>(jwtAuthorities);
        combinedAuthorities.addAll(userAuthorities);

        return new JwtAuthenticationToken(jwt, combinedAuthorities) {
            @Override
            public Object getPrincipal() {
                return usuario;
            }
        };
    }
}