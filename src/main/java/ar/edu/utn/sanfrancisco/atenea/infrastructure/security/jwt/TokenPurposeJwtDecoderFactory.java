package ar.edu.utn.sanfrancisco.atenea.infrastructure.security.jwt;

import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.claims.TokenClaims;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Objects;

@Component
public class TokenPurposeJwtDecoderFactory {

    private final SecretKey tokenSecretKey;

    public TokenPurposeJwtDecoderFactory(final SecretKey tokenSecretKey) {
        this.tokenSecretKey = tokenSecretKey;
    }

    public JwtDecoder create(final TokenPurpose expectedPurpose) {
        final NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(tokenSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        final OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(),
                purposeValidator(expectedPurpose)
        );
        decoder.setJwtValidator(validator);

        return decoder;
    }

    private OAuth2TokenValidator<Jwt> purposeValidator(final TokenPurpose expectedPurpose) {
        return token -> {
            final String purpose = token.getClaimAsString(TokenClaims.PURPOSE_FIELD);
            if (Objects.equals(expectedPurpose.name(), purpose)) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Token purpose is not " + expectedPurpose.name(), null)
            );
        };
    }
}

