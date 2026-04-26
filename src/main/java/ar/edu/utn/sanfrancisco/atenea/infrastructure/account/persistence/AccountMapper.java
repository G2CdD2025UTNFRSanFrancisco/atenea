package ar.edu.utn.sanfrancisco.atenea.infrastructure.account.persistence;

import ar.edu.utn.sanfrancisco.atenea.domain.account.*;
import ar.edu.utn.sanfrancisco.atenea.domain.account.role.Role;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.account.persistence.credential.PasswordEmbeddable;

public final class AccountMapper {

    public static Account toDomain(AccountEntity jpa) {
        return Account.reconstitute(
                new AccountId(jpa.getId()),
                new Username(jpa.getUsername()),
                new SessionVersion(jpa.getVersion()),
                jpa.getPersistenceVersion(),
                jpa.getPassword().toDomain(),
                jpa.isMfaRequired(),
                Role.valueOf(jpa.getRole()),
                jpa.getFailedLoginAttempts(),
                jpa.getCreatedAt(),
                jpa.getUpdatedAt(),
                jpa.getDeletedAt(),
                jpa.getLockedUntil()
        );
    }

    public static AccountEntity toEntity(Account domain) {
        return new AccountEntity(
                domain.getId().value(),
                domain.getUsername().value(),
                domain.getVersion().value(),
                domain.getPersistenceVersion(),
                PasswordEmbeddable.fromDomain(domain.getPassword()),
                domain.requiresMfa(),
                domain.getRole().name(),
                domain.getFailedLoginAttempts(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getDeletedAt(),
                domain.getLockedUntil()
        );
    }
}
