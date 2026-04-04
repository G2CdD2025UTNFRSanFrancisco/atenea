package ar.edu.utn.sanfrancisco.atenea.application.session.command.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.account.Username;
import ar.edu.utn.sanfrancisco.atenea.domain.session.DeviceId;

public record LoginCommand(
        Username username,
        DeviceId deviceId,
        char[] password
) {
}
