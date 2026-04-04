package ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.websocket.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.spot.OccupancyStatus;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.event.ParkingSpotStatusUpdatedEvent;

import java.time.Instant;

public record ParkingSpotStatusUpdatedWsMessage(
        String spotId,
        OccupancyStatus status,
        Instant occurredAt
) {

    public static ParkingSpotStatusUpdatedWsMessage fromEvent(final ParkingSpotStatusUpdatedEvent event) {
        return new ParkingSpotStatusUpdatedWsMessage(
                event.spotId().value(),
                event.status(),
                event.occurredAt()
        );
    }
}

