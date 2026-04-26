package ar.edu.utn.sanfrancisco.atenea.infrastructure.account.rest.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.account.role.Role;
import ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot.AccountSummarySnapshot;

public record AccountSummaryResponse(
        long id,
        String username,
        boolean mfa,
        Role role
) {

    public static AccountSummaryResponse fromSnapshot(final AccountSummarySnapshot snapshot) {
        return new AccountSummaryResponse(
                snapshot.id().value(),
                snapshot.username().value(),
                snapshot.mfa(),
                snapshot.role()
        );
    }

}
