package ar.edu.utn.sanfrancisco.atenea.infrastructure.device.rest.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.device.snapshot.DeviceDetailsSnapshot;

import java.time.Instant;

public record DeviceDetailsResponse(
        String id,
        String spot,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {

    public static DeviceDetailsResponse fromSnapshot(final DeviceDetailsSnapshot snapshot) {
        return new DeviceDetailsResponse(
                snapshot.id().value(),
                snapshot.spot().value(),
                snapshot.createdAt(),
                snapshot.updatedAt(),
                snapshot.deletedAt()
        );
    }

}
