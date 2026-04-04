package ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.persistence;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollment;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaFactor;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaPolicy;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaState;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpFactor;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.RecoveryCodeCollection;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.persistence.factor.totp.TotpFactorEntity;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.persistence.recovery.RecoveryCodeEmbeddable;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class MfaEnrollmentMapper {

    private MfaEnrollmentMapper() {
    }

    public static MfaEnrollment toDomain(final MfaEnrollmentEntity entity) {
        return MfaEnrollment.reconstitute(
                new AccountId(entity.getAccountId()),
                entity.getTotpFactor() != null
                        ? entity.getTotpFactor().toDomain()
                        : null,
                entity.getRecoveryCodes().isEmpty()
                        ? null
                        : new RecoveryCodeCollection(
                        entity.getRecoveryCodes().stream()
                        .map(RecoveryCodeEmbeddable::toDomain)
                        .collect(Collectors.toSet())
                ),
                new MfaPolicy(
                        entity.getPolicyMaxFailures(),
                        Duration.ofSeconds(entity.getPolicyLockDurationSeconds())
                ),
                MfaState.valueOf(entity.getState()),
                entity.getEnrolledAt(),
                entity.getLastVerifiedAt(),
                entity.getFailedAttempts(),
                entity.getLockedUntil()
        );
    }

    public static void updateEntity(
            final MfaEnrollmentEntity entity,
            final MfaEnrollment domain
    ) {
        entity.setState(domain.getState().name());
        entity.setEnrolledAt(domain.getEnrolledAt());
        entity.setLastVerifiedAt(domain.getLastVerifiedAt());
        entity.setFailedAttempts(domain.getFailedAttempts());
        entity.setLockedUntil(domain.getLockedUntil());

        entity.setPolicyMaxFailures(domain.getPolicy().maxFailures());
        entity.setPolicyLockDurationSeconds(domain.getPolicy().lockDuration().toSeconds());

        if (domain.getTotpFactor() != null) {
            if (entity.getTotpFactor() == null) {
                entity.assignTotpFactor(
                        TotpFactorEntity.fromDomain(entity, domain.getTotpFactor())
                );
            } else {
                updateTotpEntity(entity.getTotpFactor(), domain.getTotpFactor());
            }
        } else {
            entity.assignTotpFactor(null);
        }

        entity.getRecoveryCodes().clear();
        if (domain.getRecoveryCodes() != null) {
            entity.getRecoveryCodes().addAll(
                    domain.getRecoveryCodes().codes().stream()
                            .map(RecoveryCodeEmbeddable::fromDomain)
                            .collect(Collectors.toSet())
            );
        }
    }

    private static void updateTotpEntity(
            TotpFactorEntity entity,
            TotpFactor domain
    ) {
        entity.setSecret(domain.getSecret().value());
        entity.setIssuer(domain.getIssuer());
        entity.setLabel(domain.getLabel());
        entity.setActivatedAt(domain.getActivatedAt());
        entity.setLastUsedAt(domain.getLastUsedAt());
    }

    public static MfaEnrollmentEntity toNewEntity(final MfaEnrollment domain) {
        final MfaEnrollmentEntity entity = new MfaEnrollmentEntity(
                domain.getAccountId().value(),
                domain.getState().name(),
                domain.getEnrolledAt(),
                domain.getLastVerifiedAt(),
                domain.getFailedAttempts(),
                domain.getLockedUntil(),
                domain.getPolicy().maxFailures(),
                domain.getPolicy().lockDuration().toSeconds(),
                null,
                new HashSet<>()
        );

        if (domain.getTotpFactor() != null) {
            entity.assignTotpFactor(
                    TotpFactorEntity.fromDomain(entity, domain.getTotpFactor())
            );
        }

        if (domain.getRecoveryCodes() != null) {
            entity.getRecoveryCodes().addAll(
                    domain.getRecoveryCodes().codes().stream()
                            .map(RecoveryCodeEmbeddable::fromDomain)
                            .collect(Collectors.toSet())
            );
        }

        return entity;
    }
}



