package ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Username;

public record AccountSummarySnapshot(
        AccountId id,
        Username username,
        boolean mfa
) {

    public AccountSummarySnapshot(Long id, String username, boolean mfa) {
        this(new AccountId(id), new Username(username), mfa);
    }

}
