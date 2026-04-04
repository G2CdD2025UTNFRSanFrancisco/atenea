package ar.edu.utn.sanfrancisco.atenea.application.spot.command.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.spot.OccupancyStatus;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;

public record UpdateSpotStatusCommand(
        ParkingSpotId id,
        OccupancyStatus status
) {
}
