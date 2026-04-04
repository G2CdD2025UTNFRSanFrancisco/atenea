package ar.edu.utn.sanfrancisco.atenea.infrastructure.session.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CompleteMfaSessionRequest(
        @NotBlank(message = "totpCode is required")
        @Pattern(regexp = "\\d{6}", message = "totpCode must have exactly 6 digits")
        String totpCode
) {
}

