package ar.edu.utn.sanfrancisco.atenea.domain.device.snapshot;

import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;

import java.time.Instant;

public record DeviceDetailsSnapshot(
        DeviceId id,
        ParkingSpotId spot,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {

    public DeviceDetailsSnapshot(String id, String spot, Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this(new DeviceId(id), new ParkingSpotId(spot), createdAt, updatedAt, deletedAt);
    }
}
