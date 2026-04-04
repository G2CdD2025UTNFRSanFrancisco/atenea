package ar.edu.utn.sanfrancisco.atenea.infrastructure.device.persistence;

import ar.edu.utn.sanfrancisco.atenea.domain.device.Device;
import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.HashedSecret;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;

public final class DeviceMapper {

    public static DeviceEntity toEntity(Device domain) {
        return new DeviceEntity(
                domain.getId().value(),
                domain.getSpotId().value(),
                domain.getSecret().value(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getDeletedAt()
        );
    }

    public static Device toDomain(DeviceEntity entity) {
        return Device.reconstitute(
                new DeviceId(entity.getId()),
                new ParkingSpotId(entity.getSpotId()),
                new HashedSecret(entity.getSecretHash()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
