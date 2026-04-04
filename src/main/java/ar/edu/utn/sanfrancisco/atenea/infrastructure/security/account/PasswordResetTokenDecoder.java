package ar.edu.utn.sanfrancisco.atenea.infrastructure.security.account;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidPasswordResetTokenException;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.security.jwt.TokenPurposeJwtDecoderFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetTokenDecoder {

    private final JwtDecoder jwtDecoder;

    public PasswordResetTokenDecoder(final TokenPurposeJwtDecoderFactory tokenPurposeJwtDecoderFactory) {
        this.jwtDecoder = tokenPurposeJwtDecoderFactory.create(TokenPurpose.PASSWORD_RESET);
    }

    public AccountId accountIdFrom(final String transitionToken) {
        try {
            final Jwt jwt = jwtDecoder.decode(transitionToken);
            return new AccountId(Long.parseLong(jwt.getSubject()));
        } catch (JwtException | NumberFormatException ex) {
            throw new InvalidPasswordResetTokenException();
        }
    }

}

