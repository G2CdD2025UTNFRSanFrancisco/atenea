package ar.edu.utn.sanfrancisco.atenea.application.account.command.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.role.Role;

public record SetRoleCommand(
        AccountId actorId,
        AccountId targetId,
        Role role
) {
}

