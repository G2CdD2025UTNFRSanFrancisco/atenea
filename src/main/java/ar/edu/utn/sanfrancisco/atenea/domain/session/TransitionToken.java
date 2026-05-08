package ar.edu.utn.sanfrancisco.atenea.domain.session;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;
import ar.edu.utn.sanfrancisco.atenea.domain.identity.IdentityGenerator;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidSessionException;
import lombok.Getter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Getter
public class TransitionToken {

    private final TransitionTokenId id;
    private final AccountId accountId;
    private final DeviceId deviceId;
    private final TokenPurpose purpose;
    private final HashedRefreshToken tokenHash;
    private final Instant createdAt;
    private Instant expiresAt;
    private Instant consumedAt;
    private Instant revokedAt;

    private TransitionToken(
            final TransitionTokenId id,
            final AccountId accountId,
            final DeviceId deviceId,
            final TokenPurpose purpose,
            final HashedRefreshToken tokenHash,
            final Instant createdAt,
            final Instant expiresAt,
            final Instant consumedAt,
            final Instant revokedAt
    ) {
        this.id = id;
        this.accountId = accountId;
        this.deviceId = deviceId;
        this.purpose = purpose;
        this.tokenHash = tokenHash;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.consumedAt = consumedAt;
        this.revokedAt = revokedAt;
        validateInvariants();
    }

    public static TransitionToken create(
            final AccountId accountId,
            final DeviceId deviceId,
            final TokenPurpose purpose,
            final HashedRefreshToken tokenHash,
            final Duration duration,
            final IdentityGenerator idGenerator,
            final Clock clock
    ) {
        final Instant now = Instant.now(clock);
        return new TransitionToken(
                idGenerator.nextLong(TransitionTokenId::new),
                accountId,
                deviceId,
                purpose,
                tokenHash,
                now,
                now.plus(duration),
                null,
                null
        );
    }

    public static TransitionToken reconstitute(
            final TransitionTokenId id,
            final AccountId accountId,
            final DeviceId deviceId,
            final TokenPurpose purpose,
            final HashedRefreshToken tokenHash,
            final Instant createdAt,
            final Instant expiresAt,
            final Instant consumedAt,
            final Instant revokedAt
    ) {
        return new TransitionToken(id, accountId, deviceId, purpose, tokenHash, createdAt, expiresAt, consumedAt, revokedAt);
    }

    public boolean isActive(final Clock clock) {
        final Instant now = Instant.now(clock);
        return revokedAt == null && consumedAt == null && now.isBefore(expiresAt);
    }

    public void consume(final Clock clock) {
        if (!isActive(clock)) {
            throw new InvalidSessionException();
        }
        this.consumedAt = Instant.now(clock);
    }

    private void validateInvariants() {
        if (expiresAt.isBefore(createdAt)) {
            throw new IllegalStateException("Expiration cannot be before creation");
        }
    }
}

