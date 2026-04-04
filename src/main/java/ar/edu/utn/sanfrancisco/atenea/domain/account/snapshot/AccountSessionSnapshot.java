package ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.SessionVersion;
import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scopes;

public record AccountSessionSnapshot(
        AccountId id,
        SessionVersion version,
        Scopes scopes,
        boolean mfaEnabled
) {
    public AccountSessionSnapshot(
            Long id,
            Long version,
            long scopes,
            boolean mfaEnabled
    ) {
        this(new AccountId(id), new SessionVersion(version), new Scopes(scopes), mfaEnabled);
    }
}
