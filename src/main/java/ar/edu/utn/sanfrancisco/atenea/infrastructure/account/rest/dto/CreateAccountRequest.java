package ar.edu.utn.sanfrancisco.atenea.infrastructure.account.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
        @NotBlank(message = "username is required")
        @Size(min = 3, max = 60, message = "username length must be between 3 and 60")
        String username,

        @NotBlank(message = "password is required")
        @Size(min = 8, max = 128, message = "password length must be between 8 and 128")
        String password
) {
}

