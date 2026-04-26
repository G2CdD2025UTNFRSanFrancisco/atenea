package ar.edu.utn.sanfrancisco.atenea.infrastructure.account.rest.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.account.role.Role;
import jakarta.validation.constraints.NotNull;

public record SetRoleRequest(
        @NotNull(message = "role is required")
        Role role
) {
}

