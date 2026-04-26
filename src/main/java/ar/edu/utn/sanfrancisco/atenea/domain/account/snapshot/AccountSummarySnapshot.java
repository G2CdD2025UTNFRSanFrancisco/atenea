package ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Username;
import ar.edu.utn.sanfrancisco.atenea.domain.account.role.Role;

public record AccountSummarySnapshot(
        AccountId id,
        Username username,
        boolean mfa,
        Role role
) {

    public AccountSummarySnapshot(Long id, String username, boolean mfa, String role) {
        this(new AccountId(id), new Username(username), mfa, Role.valueOf(role));
    }

}
