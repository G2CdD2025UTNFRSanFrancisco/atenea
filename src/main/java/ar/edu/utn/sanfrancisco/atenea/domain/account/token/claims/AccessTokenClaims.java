package ar.edu.utn.sanfrancisco.atenea.domain.account.token.claims;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.role.Role;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;

import java.time.Instant;
import java.util.Map;

public record AccessTokenClaims(
        AccountId subject,
        Instant issuedAt,
        Instant expiresAt,
        Role role,
        String acr
) implements TokenClaims {

    public static final String ROLE_FIELD = "role";
    public static final String ACR_FIELD = "acr";
    public static final String ACR_MFA = "mfa";
    public static final String ACR_PASSWORD = "pwd";

    @Override
    public TokenPurpose purpose() {
        return TokenPurpose.ACCESS;
    }

    @Override
    public Map<String, Object> toMap() {
        return Map.of(
                SUBJECT_FIELD, subject.toString(),
                ISSUED_AT_FILED, issuedAt.getEpochSecond(),
                EXPIRES_AT_FIELD, expiresAt.getEpochSecond(),
                PURPOSE_FIELD, purpose().toString(),
                ROLE_FIELD, role.name(),
                ACR_FIELD, acr
        );
    }
}
