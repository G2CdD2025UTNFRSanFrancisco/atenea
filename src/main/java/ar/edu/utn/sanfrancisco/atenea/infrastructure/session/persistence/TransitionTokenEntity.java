package ar.edu.utn.sanfrancisco.atenea.infrastructure.session.persistence;

import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name = "transition_tokens")
@Getter
public class TransitionTokenEntity {

    @Id
    private Long id;
    @Column(name = "account_id", nullable = false)
    private Long accountId;
    @Column(name = "device_id", nullable = false)
    private String deviceId;
    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false)
    private TokenPurpose purpose;
    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "consumed_at")
    private Instant consumedAt;
    @Column(name = "revoked_at")
    private Instant revokedAt;

    protected TransitionTokenEntity() {}

    public TransitionTokenEntity(
            final Long id,
            final Long accountId,
            final String deviceId,
            final TokenPurpose purpose,
            final String tokenHash,
            final Instant createdAt,
            final Instant expiresAt,
            final Instant consumedAt,
            final Instant revokedAt
    ) {
        this.id = id;
        this.accountId = accountId;
        this.deviceId = deviceId;
        this.purpose = purpose;
        this.tokenHash = tokenHash;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.consumedAt = consumedAt;
        this.revokedAt = revokedAt;
    }
}

