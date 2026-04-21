package ar.edu.utn.sanfrancisco.atenea.domain.account;

import ar.edu.utn.sanfrancisco.atenea.domain.account.authentication.AuthenticationResult;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.HashedPassword;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.Password;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.PasswordHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.PlainPassword;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.*;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.MfaRequiredForHighPrivilegeException;
import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scope;
import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scopes;
import ar.edu.utn.sanfrancisco.atenea.domain.identity.IdentityGenerator;
import lombok.Getter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@Getter
public class Account {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final AccountId id;
    private final Username username;
    private SessionVersion version;

    private Password password;
    private boolean mfaRequired;
    private HierarchyLevel hierarchy;
    private Scopes scopes;
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
            final HierarchyLevel hierarchy,
            final Scopes scopes,
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
        this.hierarchy = hierarchy;
        this.scopes = scopes;
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
            final HierarchyLevel hierarchy,
            final IdentityGenerator idGen,
            final PasswordHashService hasher,
            final Clock clock
    ) {
        Instant now = clock.instant();

        Password password = new Password(
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
                hierarchy,
                Scopes.empty(),
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
            final HierarchyLevel hierarchy,
            final Scopes scopes,
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
                hierarchy,
                scopes,
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

            boolean matches = password.matches(plain, hasher);

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

    private void checkHierarchy(final Account actor) {
        if (!actor.getHierarchy().canCommand(this.hierarchy) && !actor.equals(this)) {
            throw new InsufficientHierarchyException(actor.getId(), this.id);
        }
    }

    private void checkActorHasScopes(final Account actor, final Scopes scope) {
        checkHierarchy(actor);
        if (actor.containsScopes(scope)) {
            throw new InsufficientPermissionsException(scopes.toSet());
        }
    }

    public boolean containsScope(final Scope scope) {
        return this.scopes.contains(scope);
    }

    public boolean containsScopes(final Scopes scopes) {
        return this.scopes.contains(scopes);
    }

    public boolean isAdmin() {
        return this.scopes.isAdmin();
    }

    public boolean canManageSessionsOf(Account other) {
        if (this.equals(other)) return true;
        return this.isAdmin() || this.containsScope(Scope.MANAGE_SESSIONS);
    }

    public void allows(final Account actor, final Scope requiredScope) {
        if (this.id.equals(actor.getId())) {
            return;
        }
        if (!actor.getHierarchy().canCommand(this.hierarchy)) {
            throw new InsufficientHierarchyException(actor.getId(), this.id);
        }
        if (!actor.isAdmin() && !actor.containsScope(requiredScope)) {
            throw new InsufficientPermissionsException(Set.of(requiredScope));
        }
    }

    public void grantScope(final Scope scope, final Account actor, final Clock clock) {
        if (deletedAt != null) throw new AccountDeletedException();
        checkHierarchy(actor);
        checkActorHasScopes(actor, Scopes.of(scope));
        final Set<Scope> requiresHighAssurance = scopes.getRequiresHighAssurance();
        if (!requiresHighAssurance.isEmpty() && !requiresMfa()) {
            throw new MfaRequiredForHighPrivilegeException(requiresHighAssurance);
        }
        if (!this.scopes.contains(scope)) {
            this.scopes = this.scopes.grant(scope);
            touch(Instant.now(clock));
        }
    }

    public void revokeScope(final Scope scope, final Account actor, final Clock clock) {
        if (deletedAt != null) throw new AccountDeletedException();
        checkHierarchy(actor);
        if (!actor.containsScope(scope)) throw new InsufficientPermissionsException(Set.of(scope));
        if (this.scopes.contains(scope)) {
            if (scope == Scope.ADMIN && this.equals(actor)) {
                throw new CannotRevokeOwnAdminException(this.id);
            }
            this.scopes = this.scopes.revoke(scope);
            touch(Instant.now(clock));
        }
    }

    public void setScopes(final Scopes scopes, final Account actor, final Clock clock) {
        checkHierarchy(actor);
        checkActorHasScopes(actor, scopes);
        final Set<Scope> requiresHighAssurance = scopes.getRequiresHighAssurance();
        if (!requiresHighAssurance.isEmpty() && !requiresMfa()) {
            throw new MfaRequiredForHighPrivilegeException(requiresHighAssurance);
        }
        this.scopes = scopes;
        touch(Instant.now(clock));
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
        if (!actor.getHierarchy().canCommand(this.hierarchy) && !actor.equals(this)) {
            throw new InsufficientHierarchyException(actor.getId(), this.getId());
        }
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
        if (!scopes.getRequiresHighAssurance().isEmpty() && !requiresMfa()) {
            if (this.id.value() == 0L) return; // Allow creating an initial admin account without MFA
            throw new IllegalStateException("Account with high assurance scopes must have MFA enabled");
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
