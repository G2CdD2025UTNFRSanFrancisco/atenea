package ar.edu.utn.sanfrancisco.atenea.infrastructure.session.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SessionRefreshTokenRequest(
        @NotBlank(message = "refreshToken is required")
        @Size(min = 16, max = 512, message = "refreshToken length must be between 16 and 512")
        String refreshToken
) {
}

