package ar.edu.utn.sanfrancisco.atenea.application.session.command.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.session.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.session.PlainRefreshToken;

public record RevokeCurrentSessionCommand(
        DeviceId deviceId,
        PlainRefreshToken refreshToken
) {
}
