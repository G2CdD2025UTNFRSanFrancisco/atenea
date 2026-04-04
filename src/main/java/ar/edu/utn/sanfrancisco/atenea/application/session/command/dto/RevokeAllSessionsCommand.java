package ar.edu.utn.sanfrancisco.atenea.application.session.command.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;

public record RevokeAllSessionsCommand(
        AccountId operatorId,
        AccountId targetId
) {
    public boolean isSelfOperation() {
        return operatorId.equals(targetId);
    }
}
