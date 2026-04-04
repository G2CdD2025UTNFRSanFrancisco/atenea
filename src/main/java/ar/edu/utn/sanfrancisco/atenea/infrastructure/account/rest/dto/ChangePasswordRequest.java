package ar.edu.utn.sanfrancisco.atenea.infrastructure.account.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "newPassword is required")
        @Size(min = 8, max = 128, message = "newPassword length must be between 8 and 128")
        String newPassword
) {
}

