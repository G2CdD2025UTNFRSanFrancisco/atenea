package ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.persistence;

import ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.persistence.factor.totp.TotpFactorEntity;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.persistence.recovery.RecoveryCodeEmbeddable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "mfa_enrollments")
@Getter
@Setter
public class MfaEnrollmentEntity {

    @Id
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "state", nullable = false, length = 32)
    private String state;

    @Column(name = "enrolled_at")
    private Instant enrolledAt;

    @Column(name = "last_verified_at")
    private Instant lastVerifiedAt;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "policy_max_failures", nullable = false)
    private int policyMaxFailures;

    @Column(name = "policy_lock_duration_seconds", nullable = false)
    private long policyLockDurationSeconds;

    @OneToOne(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private TotpFactorEntity totpFactor;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "mfa_recovery_codes",
            joinColumns = @JoinColumn(name = "account_id")
    )
    private Set<RecoveryCodeEmbeddable> recoveryCodes = new HashSet<>();

    protected MfaEnrollmentEntity() {
    }

    public MfaEnrollmentEntity(
            final Long accountId,
            final String state,
            final Instant enrolledAt,
            final Instant lastVerifiedAt,
            final int failedAttempts,
            final Instant lockedUntil,
            final int policyMaxFailures,
            final long policyLockDurationSeconds,
            final TotpFactorEntity totpFactor,
            final Set<RecoveryCodeEmbeddable> recoveryCodes
    ) {
        this.accountId = accountId;
        this.state = state;
        this.enrolledAt = enrolledAt;
        this.lastVerifiedAt = lastVerifiedAt;
        this.failedAttempts = failedAttempts;
        this.lockedUntil = lockedUntil;
        this.policyMaxFailures = policyMaxFailures;
        this.policyLockDurationSeconds = policyLockDurationSeconds;
        this.totpFactor = totpFactor;
        if (recoveryCodes != null) {
            this.recoveryCodes = recoveryCodes;
        }
    }

    public void assignTotpFactor(final TotpFactorEntity totpFactor) {
        this.totpFactor = totpFactor;
    }
}



