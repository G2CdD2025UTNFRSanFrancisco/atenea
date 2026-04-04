package ar.edu.utn.sanfrancisco.atenea.infrastructure.account.rest.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scope;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record SetScopesRequest(
        @NotEmpty(message = "scopes must not be empty")
        Set<Scope> scopes
) {
}

