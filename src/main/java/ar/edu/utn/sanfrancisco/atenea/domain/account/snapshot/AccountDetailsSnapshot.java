package ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Username;
import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scopes;

import java.time.Instant;

public record AccountDetailsSnapshot(
        AccountId id,
        Username username,
        Scopes scopes,
        boolean mfa,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {

    public AccountDetailsSnapshot(
            Long id,
            String username,
            long scopes,
            boolean mfa,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        this(new AccountId(id), new Username(username), new Scopes(scopes), mfa, createdAt, updatedAt, deletedAt);
    }
}
