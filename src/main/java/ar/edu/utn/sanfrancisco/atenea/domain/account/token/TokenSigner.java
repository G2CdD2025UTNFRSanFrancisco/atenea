package ar.edu.utn.sanfrancisco.atenea.domain.account.token;

import ar.edu.utn.sanfrancisco.atenea.domain.account.token.claims.TokenClaims;

public interface TokenSigner {
    String sign(final TokenClaims claims);
}
