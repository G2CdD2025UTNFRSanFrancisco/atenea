package ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp;

import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaFactor;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.EncryptedSecret;
import lombok.Getter;

import java.time.Instant;

@Getter
public final class TotpFactor implements MfaFactor {

    private final EncryptedSecret secret;
    private final String issuer;
    private final String label;

    private Instant activatedAt;
    private Instant lastUsedAt;

    public TotpFactor(
            final EncryptedSecret secret,
            final String issuer,
            final String label
    ) {
        this.secret = secret;
        this.issuer = issuer;
        this.label = label;
    }

    public boolean isActive() {
        return this.activatedAt != null;
    }

    public void activate(Instant now) {
        if (activatedAt != null) {
            throw new IllegalStateException("TOTP already activated");
        }
        activatedAt = now;
    }

    public void markUsed(Instant now) {
        lastUsedAt = now;
    }

}

