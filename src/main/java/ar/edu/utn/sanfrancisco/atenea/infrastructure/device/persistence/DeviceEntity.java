package ar.edu.utn.sanfrancisco.atenea.infrastructure.device.persistence;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name = "devices")
@Getter
public class DeviceEntity {

    @Id
    private String id;
    @Column(name = "spot_id", nullable = false)
    private String spotId;
    @Column(name = "secret_hash", nullable = false)
    private String secretHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected DeviceEntity() {}

    public DeviceEntity(
            String id,
            String spotId,
            String secret,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        this.id = id;
        this.spotId = spotId;
        this.secretHash = secret;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
}
