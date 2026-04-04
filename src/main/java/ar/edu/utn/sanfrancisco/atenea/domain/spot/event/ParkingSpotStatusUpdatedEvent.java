package ar.edu.utn.sanfrancisco.atenea.domain.spot.event;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.Event;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.OccupancyStatus;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;

import java.time.Instant;

public record ParkingSpotStatusUpdatedEvent(
        ParkingSpotId spotId,
        OccupancyStatus status,
        Instant occurredAt
) implements Event {}
