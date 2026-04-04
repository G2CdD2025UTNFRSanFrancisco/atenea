package ar.edu.utn.sanfrancisco.atenea.application.device.command.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;

public record CreateDeviceCommand(
        ParkingSpotId spotId
) {
}
