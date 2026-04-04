package ar.edu.utn.sanfrancisco.atenea.infrastructure.security.session;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidMfaChallengeTokenException;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.security.jwt.TokenPurposeJwtDecoderFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
public class MfaChallengeTokenDecoder {

    private final JwtDecoder jwtDecoder;

    public MfaChallengeTokenDecoder(final TokenPurposeJwtDecoderFactory tokenPurposeJwtDecoderFactory) {
        this.jwtDecoder = tokenPurposeJwtDecoderFactory.create(TokenPurpose.MFA_CHALLENGE);
    }

    public AccountId accountIdFrom(final String transitionToken) {
        try {
            final Jwt jwt = jwtDecoder.decode(transitionToken);
            return new AccountId(Long.parseLong(jwt.getSubject()));
        } catch (JwtException | NumberFormatException ex) {
            throw new InvalidMfaChallengeTokenException();
        }
    }

}

