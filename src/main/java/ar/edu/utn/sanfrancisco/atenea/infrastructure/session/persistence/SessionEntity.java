package ar.edu.utn.sanfrancisco.atenea.infrastructure.session.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name = "sessions")
@Getter
public class SessionEntity {

    @Id
    private Long id;
    @Column(name = "account_id", nullable = false)
    private Long accountId;
    @Column(name = "version_snapshot", nullable = false)
    private Long versionSnapshot;
    @Column(name = "device_id", nullable = false)
    private String deviceId;
    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "revoked_at")
    private Instant revokedAt;

    protected SessionEntity() {}

    public SessionEntity(
            Long id,
            Long accountId,
            String deviceId,
            Long versionSnapshot,
            String refreshToken,
            Instant createdAt,
            Instant expiresAt,
            Instant revokedAt
    ) {
        this.id = id;
        this.accountId = accountId;
        this.deviceId = deviceId;
        this.versionSnapshot = versionSnapshot;
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
    }
}