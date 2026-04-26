package ar.edu.utn.sanfrancisco.atenea.domain.account.token;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.role.Role;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.claims.AccessTokenClaims;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class DefaultAccessTokenBuilder extends AccessTokenBuilder {

    private Role role;
    private Boolean mfaEnabled;
    private static final Duration DURATION = Duration.ofMinutes(5);

    protected DefaultAccessTokenBuilder(AccountId accountId) {
        super(accountId);
    }

    public DefaultAccessTokenBuilder role(final Role role) {
        this.role = role;
        return this;
    }

    public DefaultAccessTokenBuilder mfaEnabled(final boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
        return this;
    }

    @Override
    protected void validate() {
        Objects.requireNonNull(getAccountId());
        Objects.requireNonNull(role);
        Objects.requireNonNull(mfaEnabled);
    }

    @Override
    public String sign(final TokenSigner signer, final Clock clock) {
        validate();
        final Instant now = Instant.now(clock);
        return signer.sign(new AccessTokenClaims(
                getAccountId(),
                now,
                now.plus(DURATION),
                role,
                mfaEnabled ? AccessTokenClaims.ACR_MFA : AccessTokenClaims.ACR_PASSWORD
        ));
    }
}
