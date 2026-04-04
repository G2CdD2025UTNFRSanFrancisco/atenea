package ar.edu.utn.sanfrancisco.atenea.domain.account.token.claims;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;

import java.time.Instant;
import java.util.Map;

public interface TokenClaims {

    String SUBJECT_FIELD = "sub";
    String ISSUER_FIELD = "iss";
    String ISSUED_AT_FILED = "iat";
    String EXPIRES_AT_FIELD = "exp";
    String PURPOSE_FIELD = "typ";

    AccountId subject();
    Instant issuedAt();
    Instant expiresAt();
    TokenPurpose purpose();

    Map<String, Object> toMap();
}
