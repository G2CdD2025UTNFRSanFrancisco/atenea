package ar.edu.utn.sanfrancisco.atenea.application.account.command.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scope;

import java.util.Set;

public record SetScopesCommand(
        AccountId actorId,
        AccountId targetId,
        Set<Scope> scopes
) {
}
