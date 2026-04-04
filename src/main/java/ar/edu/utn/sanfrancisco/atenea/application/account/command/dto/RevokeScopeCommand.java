package ar.edu.utn.sanfrancisco.atenea.application.account.command.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scope;

public record RevokeScopeCommand(
        AccountId actorId,
        AccountId targetId,
        Scope scope
) {
}
