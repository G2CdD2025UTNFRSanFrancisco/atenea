package ar.edu.utn.sanfrancisco.atenea.domain.account.token;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.claims.PasswordResetTokenClaims;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class PasswordResetAccessTokenBuilder extends AccessTokenBuilder {

    private static final Duration DURATION = Duration.ofMinutes(2);

    protected PasswordResetAccessTokenBuilder(AccountId accountId) {
        super(accountId);
    }

    @Override
    protected void validate() {
        Objects.requireNonNull(getAccountId());
    }

    @Override
    public String sign(TokenSigner signer, Clock clock) {
        validate();
        final Instant now = Instant.now(clock);
        return signer.sign(new PasswordResetTokenClaims(
                getAccountId(),
                now,
                now.plus(DURATION)
        ));
    }
}
