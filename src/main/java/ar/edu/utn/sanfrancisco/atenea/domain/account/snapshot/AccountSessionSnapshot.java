package ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.SessionVersion;
import ar.edu.utn.sanfrancisco.atenea.domain.account.role.Role;

public record AccountSessionSnapshot(
        AccountId id,
        SessionVersion version,
        Role role,
        boolean mfaEnabled
) {
    public AccountSessionSnapshot(
            Long id,
            Long version,
            String role,
            boolean mfaEnabled
    ) {
        this(new AccountId(id), new SessionVersion(version), Role.valueOf(role), mfaEnabled);
    }
}
