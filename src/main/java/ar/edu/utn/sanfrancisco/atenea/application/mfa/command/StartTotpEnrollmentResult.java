package ar.edu.utn.sanfrancisco.atenea.application.mfa.command;

import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;

public record StartTotpEnrollmentResult(
        PlainSecret secret,
        String otpauthUri,
        String issuer,
        String accountLabel
) {
    public String manualEntryKey() {
        return secret.toString();
    }
}

