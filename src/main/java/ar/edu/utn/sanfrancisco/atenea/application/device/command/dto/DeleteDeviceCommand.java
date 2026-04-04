package ar.edu.utn.sanfrancisco.atenea.application.device.command.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceId;

public record DeleteDeviceCommand(
        DeviceId deviceId
) {
}
