package ar.edu.utn.sanfrancisco.atenea.infrastructure.session.persistence;

import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface SpringDataTransitionTokenRepository extends JpaRepository<TransitionTokenEntity, Long> {

    Optional<TransitionTokenEntity> findFirstByTokenHashAndPurposeAndDeviceIdAndConsumedAtIsNullAndRevokedAtIsNullAndExpiresAtAfter(
            String tokenHash,
            TokenPurpose purpose,
            String deviceId,
            Instant now
    );
}

