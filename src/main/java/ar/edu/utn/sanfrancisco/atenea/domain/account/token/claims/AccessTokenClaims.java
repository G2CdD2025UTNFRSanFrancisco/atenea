package ar.edu.utn.sanfrancisco.atenea.domain.account.token.claims;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scopes;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;

import java.time.Instant;
import java.util.Map;

public record AccessTokenClaims(
        AccountId subject,
        Instant issuedAt,
        Instant expiresAt,
        Scopes scopes,
        String acr
) implements TokenClaims {

    public static final String SCOPES_FIELD = "scp";
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
                SCOPES_FIELD, scopes.value(),
                ACR_FIELD, acr
        );
    }
}
