package ar.edu.utn.sanfrancisco.atenea.domain.mfa;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions.*;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpCode;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpFactor;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpService;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.HashedRecoveryCode;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.RecoveryCodeCollection;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.SecretEncryptionService;
import lombok.Getter;

import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public final class MfaEnrollment {

    private final AccountId accountId;
    private TotpFactor totpFactor;
    private RecoveryCodeCollection recoveryCodes;

    private MfaState state;
    private Instant enrolledAt;
    private Instant lastVerifiedAt;

    private final MfaPolicy policy;
    private int failedAttempts;
    private Instant lockedUntil;

    public MfaEnrollment(AccountId accountId, MfaPolicy policy) {
        this.accountId = Objects.requireNonNull(accountId);
        this.policy = Objects.requireNonNull(policy);
        this.state = MfaState.NOT_ENROLLED;
    }

    public static MfaEnrollment reconstitute(
            final AccountId accountId,
            final TotpFactor totpFactor,
            final RecoveryCodeCollection recoveryCodes,
            final MfaPolicy policy,
            final MfaState state,
            final Instant enrolledAt,
            final Instant lastVerifiedAt,
            final int failedAttempts,
            final Instant lockedUntil
    ) {
        final MfaEnrollment enrollment = new MfaEnrollment(accountId, policy);
        enrollment.recoveryCodes = recoveryCodes;
        enrollment.totpFactor = totpFactor;
        enrollment.state = Objects.requireNonNull(state);
        enrollment.enrolledAt = enrolledAt;
        enrollment.lastVerifiedAt = lastVerifiedAt;
        enrollment.failedAttempts = failedAttempts;
        enrollment.lockedUntil = lockedUntil;
        return enrollment;
    }

    public void startTotpEnrollment(
            final PlainSecret secret,
            final String issuer,
            final String label,
            final Instant now,
            final SecretEncryptionService secretEncryptionService) {
        ensureState(MfaState.NOT_ENROLLED);

        this.totpFactor = new TotpFactor(
                secretEncryptionService.encrypt(secret),
                issuer,
                label
        );
        this.state = MfaState.PENDING_ACTIVATION;
        this.enrolledAt = now;
    }

    public void activateTotp(
            final RecoveryCodeCollection codes,
            final Instant now
    ) {
        ensureState(MfaState.PENDING_ACTIVATION);

        this.totpFactor.activate(now);
        this.recoveryCodes = codes;
        this.state = MfaState.ACTIVE;
        this.failedAttempts = 0;
    }

    public boolean requiresVerification(final Instant now) {
        unlockIfExpired(now);
        return state == MfaState.ACTIVE;
    }

    public void verifySuccess(Instant now) {
        ensureActive();
        failedAttempts = 0;
        lastVerifiedAt = now;
    }

    public void verifyFailure(Instant now) {
        ensureActive();
        failedAttempts++;
        if (failedAttempts >= policy.maxFailures()) {
            lock(now);
        }
    }

    public void verifyRecoveryCode(HashedRecoveryCode code, Instant now) {
        ensureActive();
        recoveryCodes.use(code);
        failedAttempts = 0;
        lastVerifiedAt = now;
    }

    public void verifyTotp(
            final TotpCode code,
            final TotpService totp,
            final SecretEncryptionService encryptionService,
            final Clock clock
    ) {
        ensureActive();

        if (totpFactor == null || !totpFactor.isActive()) {
            throw new MfaNotEnrolledException();
        }

        final Instant now = Instant.now(clock);
        PlainSecret secret = encryptionService.decrypt(totpFactor.getSecret());

        if (!totp.verify(secret, code, now)) {
            throw new InvalidTotpCodeException();
        }

        failedAttempts = 0;
        lastVerifiedAt = now;
    }

    public void lock(Instant now) {
        state = MfaState.LOCKED;
        lockedUntil = now.plus(policy.lockDuration());
    }

    public void unlockIfExpired(Instant now) {
        if (state == MfaState.LOCKED && now.isAfter(lockedUntil)) {
            state = MfaState.ACTIVE;
            failedAttempts = 0;
            lockedUntil = null;
        }
    }

    public void disable() {
        ensureNot(MfaState.NOT_ENROLLED);
        recoveryCodes = null;
        state = MfaState.DISABLED;
    }

    public void regenerateRecoveryCodes(RecoveryCodeCollection hashed) {
        ensureActive();
        this.recoveryCodes = hashed;
    }

    private void ensureActive() {
        if (state == MfaState.LOCKED) {
            throw new MfaLockedException(lockedUntil);
        }
        if (state == MfaState.NOT_ENROLLED) {
            throw new MfaNotEnrolledException();
        }
        if (state == MfaState.DISABLED) {
            throw new MfaDisabledException();
        }
        if (state != MfaState.ACTIVE) {
            throw new MfaNotActiveException(state);
        }
    }


    private void ensureState(MfaState expected) {
        if (state != expected) {
            throw new InvalidMfaStateTransitionException(state, "expected: " + expected);
        }
    }

    private void ensureNot(MfaState forbidden) {
        if (state == forbidden) {
            throw new InvalidMfaStateTransitionException(state, "forbidden: " + forbidden);
        }
    }


}
