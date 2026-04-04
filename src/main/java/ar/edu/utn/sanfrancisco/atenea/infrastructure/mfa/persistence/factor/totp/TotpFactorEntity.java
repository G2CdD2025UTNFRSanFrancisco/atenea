package ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.persistence.factor.totp;

import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpFactor;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.EncryptedSecret;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.persistence.MfaEnrollmentEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "mfa_totp_factors")
@Getter
@Setter
public class TotpFactorEntity {

    @Id
    @Column(name = "account_id")
    private Long accountId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private MfaEnrollmentEntity enrollment;

    @Column(name = "secret", nullable = false)
    private String secret;

    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "activated_at")
    private Instant activatedAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    protected TotpFactorEntity() {
    }

    public TotpFactorEntity(
            final MfaEnrollmentEntity enrollment,
            final String secret,
            final String issuer,
            final String label,
            final Instant activatedAt,
            final Instant lastUsedAt
    ) {
        this.enrollment = enrollment;
        this.secret = secret;
        this.issuer = issuer;
        this.label = label;
        this.activatedAt = activatedAt;
        this.lastUsedAt = lastUsedAt;
    }

    public TotpFactor toDomain() {
        final TotpFactor factor = new TotpFactor(new EncryptedSecret(secret), issuer, label);
        if (activatedAt != null) {
            factor.activate(activatedAt);
        }
        if (lastUsedAt != null) {
            factor.markUsed(lastUsedAt);
        }
        return factor;
    }

    public static TotpFactorEntity fromDomain(final MfaEnrollmentEntity enrollment, final TotpFactor factor) {
        return new TotpFactorEntity(
                enrollment,
                factor.getSecret().value(),
                factor.getIssuer(),
                factor.getLabel(),
                factor.getActivatedAt(),
                factor.getLastUsedAt()
        );
    }
}

