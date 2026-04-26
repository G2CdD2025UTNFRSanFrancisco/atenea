package ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Username;
import ar.edu.utn.sanfrancisco.atenea.domain.account.role.Role;

import java.time.Instant;

public record AccountDetailsSnapshot(
        AccountId id,
        Username username,
        Role role,
        boolean mfa,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {

    public AccountDetailsSnapshot(
            Long id,
            String username,
            String role,
            boolean mfa,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        this(new AccountId(id), new Username(username), Role.valueOf(role), mfa, createdAt, updatedAt, deletedAt);
    }
}
