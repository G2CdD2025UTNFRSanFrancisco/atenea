package ar.edu.utn.sanfrancisco.atenea.domain.session;

import ar.edu.utn.sanfrancisco.atenea.domain.account.Account;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.SessionVersion;
import ar.edu.utn.sanfrancisco.atenea.domain.identity.IdentityGenerator;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidSessionException;
import lombok.Getter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Getter
public class Session {

    private static final Duration ORIGINAL_DURATION = Duration.ofDays(1);
    private static final Duration RENEWAL_THRESHOLD = Duration.ofHours(6);

    private final SessionId id;
    private final AccountId accountId;
    private final DeviceId deviceId;
    private final SessionVersion versionSnapshot;

    private HashedRefreshToken refreshToken;

    private final Instant createdAt;
    private Instant expiresAt;
    private Instant revokedAt;

    private Session(
            final SessionId id,
            final AccountId accountId,
            final DeviceId deviceId,
            final SessionVersion versionSnapshot,
            final HashedRefreshToken refreshToken,
            final Instant createdAt,
            final Instant expiresAt,
            final Instant revokedAt
    ) {
        this.id = id;
        this.accountId = accountId;
        this.deviceId = deviceId;
        this.versionSnapshot = versionSnapshot;
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;

        validateInvariants();
    }

    public static Session create(
            final Account account,
            final DeviceId deviceId,
            final PlainRefreshToken plainRefreshToken,
            final IdentityGenerator idGen,
            final RefreshTokenHashService hasher,
            final Clock clock
    ) {
        final Instant now = Instant.now(clock);
        final Instant expires = now.plus(ORIGINAL_DURATION);
        return new Session(
                idGen.nextLong(SessionId::new),
                account.getId(),
                deviceId,
                account.getVersion(),
                hasher.hash(plainRefreshToken),
                now,
                expires,
                null
        );
    }

    public static Session reconstitute(
            final SessionId id,
            final AccountId accountId,
            final DeviceId deviceId,
            final SessionVersion versionSnapshot,
            final HashedRefreshToken refreshToken,
            final Instant createdAt,
            final Instant expiresAt,
            final Instant revokedAt
    ) {
        return new Session(
                id,
                accountId,
                deviceId,
                versionSnapshot,
                refreshToken,
                createdAt,
                expiresAt,
                revokedAt
        );
    }

    public boolean isActive(final Clock clock) {
        final Instant now = Instant.now(clock);
        return revokedAt == null && now.isBefore(expiresAt);
    }

    public boolean isExpired(final Clock clock) {
        return Instant.now(clock).isAfter(expiresAt);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public void rotateRefreshToken(
            final PlainRefreshToken newToken,
            final RefreshTokenHashService hasher,
            final Clock clock
    ) {
        if (!isActive(clock)) {
            throw new InvalidSessionException();
        }
        this.refreshToken = hasher.hash(newToken);
        renewSessionIfNecessary(clock);
    }

    private void renewSessionIfNecessary(final Clock clock) {
        final Instant now = Instant.now(clock);
        final Duration remaining = Duration.between(now, expiresAt);

        if (remaining.compareTo(RENEWAL_THRESHOLD) <= 0) {
            this.expiresAt = now.plus(ORIGINAL_DURATION);
        }
    }

    public void revoke(final Clock clock) {
        if (revokedAt != null) {
            return;
        }
        this.revokedAt = Instant.now(clock);
    }

    private void validateInvariants() {
        if (expiresAt.isBefore(createdAt)) {
            throw new IllegalStateException("Expiration cannot be before creation");
        }
    }
}
