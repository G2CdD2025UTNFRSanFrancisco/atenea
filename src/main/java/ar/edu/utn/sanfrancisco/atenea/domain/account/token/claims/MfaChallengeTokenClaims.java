package ar.edu.utn.sanfrancisco.atenea.domain.account.token.claims;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;

import java.time.Instant;
import java.util.Map;

public record MfaChallengeTokenClaims(
        AccountId subject,
        Instant issuedAt,
        Instant expiresAt
) implements TokenClaims {

    @Override
    public TokenPurpose purpose() {
        return TokenPurpose.MFA_CHALLENGE;
    }

    @Override
    public Map<String, Object> toMap() {
        return Map.of(
                SUBJECT_FIELD, subject.toString(),
                ISSUED_AT_FILED, issuedAt.getEpochSecond(),
                EXPIRES_AT_FIELD, expiresAt.getEpochSecond(),
                PURPOSE_FIELD, purpose().toString()
        );
    }
}
