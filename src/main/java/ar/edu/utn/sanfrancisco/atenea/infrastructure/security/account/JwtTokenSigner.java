package ar.edu.utn.sanfrancisco.atenea.infrastructure.security.account;

import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenSigner;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.claims.TokenClaims;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenSigner implements TokenSigner {

    private final JwtEncoder jwtEncoder;

    public JwtTokenSigner(final JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String sign(final TokenClaims claims) {
        final JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                .subject(claims.subject().toString())
                .issuedAt(claims.issuedAt())
                .expiresAt(claims.expiresAt())
                .claim(TokenClaims.PURPOSE_FIELD, claims.purpose().name());

        claims.toMap().forEach((claim, value) -> {
            if (!TokenClaims.SUBJECT_FIELD.equals(claim)
                    && !TokenClaims.ISSUED_AT_FILED.equals(claim)
                    && !TokenClaims.EXPIRES_AT_FIELD.equals(claim)
                    && !TokenClaims.PURPOSE_FIELD.equals(claim)) {
                builder.claim(claim, value);
            }
        });

        return jwtEncoder.encode(JwtEncoderParameters.from(builder.build())).getTokenValue();
    }
}


