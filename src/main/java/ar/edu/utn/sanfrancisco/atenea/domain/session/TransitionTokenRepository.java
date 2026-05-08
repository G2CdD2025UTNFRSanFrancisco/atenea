package ar.edu.utn.sanfrancisco.atenea.domain.session;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;

import java.time.Instant;
import java.util.Optional;

public interface TransitionTokenRepository {

    Optional<TransitionToken> findActiveByHashAndPurposeAndDeviceId(
            final HashedRefreshToken tokenHash,
            final TokenPurpose purpose,
            final DeviceId deviceId,
            final Instant now
    );

    void create(final TransitionToken token);
    void update(final TransitionToken token);

    void revokeAllByAccountId(final AccountId accountId);
}

