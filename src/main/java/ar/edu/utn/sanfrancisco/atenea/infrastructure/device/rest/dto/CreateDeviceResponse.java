package ar.edu.utn.sanfrancisco.atenea.infrastructure.device.rest.dto;

import ar.edu.utn.sanfrancisco.atenea.application.device.command.dto.CreateDeviceResult;

public record CreateDeviceResponse(
        String deviceId,
        char[] secret
) {

    public static CreateDeviceResponse fromResult(final CreateDeviceResult result) {
        return new CreateDeviceResponse(
                result.deviceId().value(),
                result.secret().value()
        );
    }
}
