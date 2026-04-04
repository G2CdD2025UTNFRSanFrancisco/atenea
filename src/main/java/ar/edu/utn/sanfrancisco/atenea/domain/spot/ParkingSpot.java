package ar.edu.utn.sanfrancisco.atenea.domain.spot;

import ar.edu.utn.sanfrancisco.atenea.domain.spot.event.ParkingSpotStatusUpdatedEvent;
import lombok.Getter;

import java.time.Clock;
import java.time.Instant;

@Getter
public class ParkingSpot {

    private final ParkingSpotId id;
    private final Coordinate coordinate;
    private OccupancyStatus status;
    private Instant lastChangedAt;

    public ParkingSpot(
            final ParkingSpotId id,
            final Coordinate coordinate,
            final OccupancyStatus status,
            final Instant lastChangedAt
    ) {
        this.id = id;
        this.coordinate = coordinate;
        this.status = status;
        this.lastChangedAt = lastChangedAt;
    }

    public ParkingSpotStatusUpdatedEvent update(
            final OccupancyStatus status,
            final Clock clock
    ) {
        if (this.status.equals(status)) return null;
        final Instant now = Instant.now(clock);
        this.status = status;
        this.lastChangedAt = now;
        return new ParkingSpotStatusUpdatedEvent(
                this.id,
                this.status,
                now
        );
    }
}
