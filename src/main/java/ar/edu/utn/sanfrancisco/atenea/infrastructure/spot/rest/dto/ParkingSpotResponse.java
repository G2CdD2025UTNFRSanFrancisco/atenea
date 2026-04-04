package ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.rest.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpot;

import java.time.Instant;

public record ParkingSpotResponse(
        String id,
        int col,
        int row,
        String status,
        Instant lastChangedAt
) {

    public static ParkingSpotResponse fromDomain(final ParkingSpot spot) {
        return new ParkingSpotResponse(
                spot.getId().value(),
                spot.getCoordinate().col(),
                spot.getCoordinate().row(),
                spot.getStatus().name(),
                spot.getLastChangedAt()
        );
    }
}

