package ar.edu.utn.sanfrancisco.atenea.infrastructure.security;

import ar.edu.utn.sanfrancisco.atenea.domain.account.role.Role;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.claims.AccessTokenClaims;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
public class AccessConfiguration {

    @Bean
    public BearerTokenResolver authenticationHeaderTokenResolver() {
        final DefaultBearerTokenResolver resolver = new DefaultBearerTokenResolver();
        resolver.setBearerTokenHeaderName("Authorization");
        return resolver;
    }

    @Bean
    public SecurityFilterChain apiChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            BearerTokenResolver authenticationHeaderTokenResolver,
            CorsConfigurationSource corsConfigurationSource
    ) {
        http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/spots").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/sessions").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/sessions/mfa").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/accounts/password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/sessions/refresh").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/sessions").permitAll()
                        .requestMatchers("/api/v1/spots/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(authenticationHeaderTokenResolver)
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(accessTokenJwtAuthenticationConverter())
                        )
                );

        return http.build();
    }


    private Converter<Jwt, Collection<GrantedAuthority>> accessTokenAuthoritiesConverter() {
        return jwt -> {
            final List<GrantedAuthority> authorities = new ArrayList<>();
            final String roleClaim = jwt.getClaimAsString(AccessTokenClaims.ROLE_FIELD);
            if (roleClaim != null && !roleClaim.isBlank()) {
                try {
                    final Role role = Role.valueOf(roleClaim);
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
                } catch (IllegalArgumentException ignored) {
                    // Ignore invalid role claims.
                }
            }

            final String acr = jwt.getClaimAsString(AccessTokenClaims.ACR_FIELD);
            if (acr != null && !acr.isBlank()) {
                authorities.add(new SimpleGrantedAuthority("ACR_" + acr));
            }
            return authorities;
        };
    }

    private JwtAuthenticationConverter accessTokenJwtAuthenticationConverter() {
        final JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(accessTokenAuthoritiesConverter());
        converter.setPrincipalClaimName("sub");
        return converter;
    }


}
