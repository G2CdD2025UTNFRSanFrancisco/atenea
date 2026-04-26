package ar.edu.utn.sanfrancisco.atenea.domain.account;

import ar.edu.utn.sanfrancisco.atenea.domain.account.authentication.AuthenticationResult;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.HashedPassword;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.Password;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.PasswordHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.PlainPassword;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.*;
import ar.edu.utn.sanfrancisco.atenea.domain.account.role.Role;
import ar.edu.utn.sanfrancisco.atenea.domain.identity.IdentityGenerator;
import lombok.Getter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Getter
public class Account {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final AccountId id;
    private final Username username;
    private SessionVersion version;

    private Password password;
    private boolean mfaRequired;
    private Role role;
    private int failedLoginAttempts;

    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Instant lockedUntil;

    private Long persistenceVersion;

    private Account(
            final AccountId id,
            final Username username,
            final SessionVersion version,
            final Long persistenceVersion,
            final Password password,
            final boolean mfaRequired,
            final Role role,
            final int failedLoginAttempts,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt,
            final Instant lockedUntil
    ) {
        this.id = id;
        this.username = username;
        this.version = version;
        this.persistenceVersion = persistenceVersion;
        this.password = password;
        this.mfaRequired = mfaRequired;
        this.role = role;
        this.failedLoginAttempts = failedLoginAttempts;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.lockedUntil = lockedUntil;

        validateInvariants();
    }

    public static Account create(
            final Username username,
            final PlainPassword plain,
            final IdentityGenerator idGen,
            final PasswordHashService hasher,
            final Clock clock
    ) {
        final Instant now = clock.instant();

        final Password password = new Password(
                hasher.hash(plain),
                true,
                now
        );

        return new Account(
                idGen.nextLong(AccountId::new),
                username,
                new SessionVersion(0L),
                null,
                password,
                false,
                Role.USER,
                0,
                now,
                now,
                null,
                null
        );
    }

    public static Account reconstitute(
            final AccountId id,
            final Username username,
            final SessionVersion version,
            final Long persistenceVersion,
            final Password password,
            final boolean mfaRequired,
            final Role role,
            final int failedLoginAttempts,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt,
            final Instant lockedUntil
    ) {
        return new Account(
                id,
                username,
                version,
                persistenceVersion,
                password,
                mfaRequired,
                role,
                failedLoginAttempts,
                createdAt,
                updatedAt,
                deletedAt,
                lockedUntil
        );
    }

    public void canAuthenticate(final Clock clock) {
        final Instant now = Instant.now(clock);
        if (this.deletedAt != null) throw new AccountDeletedException();
        if (this.lockedUntil != null && now.isBefore(this.lockedUntil)) throw new AccountLockedException(this.lockedUntil);
    }

    public boolean requiresMfa() {
        return this.mfaRequired;
    }

    public AuthenticationResult authenticatePassword(
            final PlainPassword plain,
            final PasswordHashService hasher,
            final Clock clock
    ) {
        try (plain) {
            final Instant now = clock.instant();

            if (deletedAt != null) {
                return new AuthenticationResult.Deleted();
            }

            if (lockedUntil != null && now.isBefore(lockedUntil)) {
                return new AuthenticationResult.Locked(lockedUntil);
            }

            final boolean matches = password.matches(plain, hasher);

            if (!matches) {
                registerFailedAttempt(now);
                return new AuthenticationResult.InvalidCredentials();
            }

            registerSuccessfulLogin(clock);

            if (password.isChangeRequired()) {
                return new AuthenticationResult.PasswordChangeRequired();
            }

            return new AuthenticationResult.PasswordVerified();
        }
    }

    public void invalidateSessions(final Clock clock) {
        bumpVersion();
        touch(Instant.now(clock));
    }

    private void ensureActorCanOperateOnTarget(final Account actor) {
        if (actor.equals(this)) {
            return;
        }
        if (!actor.getRole().canOperateOn(this.role)) {
            throw new InsufficientPermissionsException("role hierarchy");
        }
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isOwner() {
        return this.role == Role.OWNER;
    }

    public boolean canManageSessionsOf(final Account other) {
        if (this.equals(other)) return true;
        return this.role.canOperateOn(other.role);
    }

    public void allowsRoleManagementBy(final Account actor) {
        ensureActorCanOperateOnTarget(actor);
        if (!actor.getRole().canManageAccounts()) {
            throw new InsufficientPermissionsException("ROLE_ADMIN or ROLE_OWNER");
        }
    }

    public void setRole(final Role role, final Account actor, final Clock clock) {
        if (deletedAt != null) throw new AccountDeletedException();
        ensureActorCanOperateOnTarget(actor);
        if (!actor.getRole().canManageAccounts()) {
            throw new InsufficientPermissionsException("ROLE_ADMIN or ROLE_OWNER");
        }
        if (role.requiresHighAssurance() && !this.mfaRequired) {
            throw new MfaRequiredForHighPrivilegeException(role.name());
        }
        if (this.role != role) {
            this.role = role;
            validateInvariants();
            touch(Instant.now(clock));
        }
    }

    public void setMfaRequired(final boolean mfaRequired) {
        if (!mfaRequired && this.role.requiresHighAssurance()) {
            throw new CannotDisableMfaWithHighPrivilegesException(this.role.name());
        }
        this.mfaRequired = mfaRequired;
        validateInvariants();
    }

    public void changePassword(
            final PlainPassword plain,
            final PasswordHashService hasher,
            final Clock clock
    ) {
        final Instant now = Instant.now(clock);
        final HashedPassword hashed = hasher.hash(plain);
        this.password = this.password.changeTo(hashed, now);
        bumpVersion();
        touch(now);
    }

    public void delete(final Account actor, final Clock clock) {
        if (deletedAt != null) {
            throw new AccountAlreadyDeletedException();
        }
        ensureActorCanOperateOnTarget(actor);
        this.deletedAt = clock.instant();
        bumpVersion();
    }

    public void restore(final Clock clock) {
        if (deletedAt == null) {
            throw new AccountNotDeletedException();
        }

        this.deletedAt = null;
        bumpVersion();
        touch(Instant.now(clock));
    }

    public boolean requiresChangePassword() {
        return this.password.isChangeRequired();
    }

    public void requirePasswordChange() {
        this.password = this.password.markChangeRequired();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    private void validateInvariants() {
        if (role == null) {
            throw new IllegalStateException("Role is required");
        }
        if (this.role.requiresHighAssurance() && !requiresMfa()) {
            if (this.id.value() == 0L) return; // Allow bootstrap owner before MFA enrollment.
            throw new IllegalStateException("Account with role " + role + " must have MFA enabled");
        }
    }

    private void bumpVersion() {
        this.version = this.version.next();
    }

    private void registerFailedAttempt(final Instant now) {
        failedLoginAttempts++;
        if (failedLoginAttempts >= MAX_ATTEMPTS) {
            lockedUntil = now.plus(LOCK_DURATION);
            bumpVersion();
        }
    }

    private void registerSuccessfulLogin(final Clock clock) {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        touch(Instant.now(clock));
    }

    private void touch(final Instant now) {
        this.updatedAt = now;
    }
}
