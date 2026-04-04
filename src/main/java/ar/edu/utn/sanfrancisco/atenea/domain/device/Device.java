package ar.edu.utn.sanfrancisco.atenea.domain.device;

import ar.edu.utn.sanfrancisco.atenea.domain.identity.IdentityGenerator;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.HashedSecret;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.SecretHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.device.exception.DeviceAlreadyDeletedException;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;
import lombok.Getter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Getter
public class Device {

    private final DeviceId id;
    private final ParkingSpotId spotId;
    private final HashedSecret secret;

    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Device(
            final DeviceId id,
            final ParkingSpotId spotId,
            final HashedSecret secret,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        this.id = id;
        this.spotId = spotId;
        this.secret = secret;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Device create(
            final ParkingSpotId spotId,
            final PlainSecret plain,
            final IdentityGenerator idGen,
            final SecretHashService hasher,
            final Clock clock
    ) {
        final Instant now = Instant.now(clock);
        return new Device(
                idGen.nextString(DeviceId::new),
                spotId,
                hasher.hash(plain),
                now,
                now,
                null
        );
    }

    public static Device reconstitute(
            final DeviceId id,
            final ParkingSpotId spotId,
            final HashedSecret secret,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        return new Device(
                id,
                spotId,
                secret,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    public void delete(final Clock clock) {
        if (deletedAt != null) {
            throw new DeviceAlreadyDeletedException();
        }
        this.deletedAt = clock.instant();
    }

    private void touch(final Instant now) {
        this.updatedAt = now;
    }
}
