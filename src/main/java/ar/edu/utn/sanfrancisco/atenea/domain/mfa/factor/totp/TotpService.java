package ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp;

import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;

import java.time.Instant;

public interface TotpService {

    PlainSecret generateSecret();

    boolean verify(
            final PlainSecret secret,
            final TotpCode code,
            final Instant at
    );

    String buildOtpAuthUri(
            final String issuer,
            final String accountName,
            final PlainSecret secret
    );
}

