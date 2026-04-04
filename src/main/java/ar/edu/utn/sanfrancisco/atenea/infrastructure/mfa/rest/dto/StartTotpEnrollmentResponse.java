package ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.rest.dto;

public record StartTotpEnrollmentResponse(
        String otpauthUri,
        String manualEntryKey,
        String issuer,
        String accountLabel
) {
}

