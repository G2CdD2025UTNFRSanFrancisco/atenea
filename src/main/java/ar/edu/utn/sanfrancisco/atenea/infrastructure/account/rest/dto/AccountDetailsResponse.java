package ar.edu.utn.sanfrancisco.atenea.infrastructure.account.rest.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot.AccountDetailsSnapshot;

import java.time.Instant;

public record AccountDetailsResponse (
    long id,
    String username,
    String role,
    boolean mfa,
    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt
) {

    public static AccountDetailsResponse fromSnapshot(final AccountDetailsSnapshot snapshot) {
        return new AccountDetailsResponse(
                snapshot.id().value(),
                snapshot.username().value(),
                snapshot.role().name(),
                snapshot.mfa(),
                snapshot.createdAt(),
                snapshot.updatedAt(),
                snapshot.deletedAt()
        );
    }

}
