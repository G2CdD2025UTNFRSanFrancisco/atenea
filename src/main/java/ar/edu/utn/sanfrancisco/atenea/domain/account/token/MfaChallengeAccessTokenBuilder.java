package ar.edu.utn.sanfrancisco.atenea.domain.account.token;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.claims.MfaChallengeTokenClaims;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class MfaChallengeAccessTokenBuilder extends AccessTokenBuilder {

    private static final Duration DURATION = Duration.ofMinutes(2);

    protected MfaChallengeAccessTokenBuilder(AccountId accountId) {
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
        return signer.sign(new MfaChallengeTokenClaims(
                getAccountId(),
                now,
                now.plus(DURATION)
        ));
    }
}
