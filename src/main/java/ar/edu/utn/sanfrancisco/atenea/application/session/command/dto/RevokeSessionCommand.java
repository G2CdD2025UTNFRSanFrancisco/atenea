package ar.edu.utn.sanfrancisco.atenea.application.session.command.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.session.SessionId;

public record RevokeSessionCommand(
        AccountId operatorId,
        SessionId sessionId
) {
}
