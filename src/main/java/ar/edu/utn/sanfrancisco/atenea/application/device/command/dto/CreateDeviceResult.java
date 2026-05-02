package ar.edu.utn.sanfrancisco.atenea.application.device.command.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;

public record CreateDeviceResult(
        DeviceId deviceId,
        PlainSecret secret
) {
}
