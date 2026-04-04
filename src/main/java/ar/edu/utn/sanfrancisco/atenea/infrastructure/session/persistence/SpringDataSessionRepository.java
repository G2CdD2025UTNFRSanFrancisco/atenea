package ar.edu.utn.sanfrancisco.atenea.infrastructure.session.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface SpringDataSessionRepository extends JpaRepository<SessionEntity, Long> {

    Optional<SessionEntity> findByAccountIdAndDeviceIdAndRevokedAtIsNull(Long accountId, String deviceId);
    Optional<SessionEntity> findByRefreshTokenAndDeviceIdAndRevokedAtIsNull(String refreshToken, String deviceId);
    Optional<SessionEntity> findByIdAndRevokedAtIsNull(Long id);

    @Modifying
    @Query("""
            update SessionEntity s
            set s.revokedAt = :now
            where s.accountId = :accountId
              and s.revokedAt is null
            """)
    int revokeAllByAccountId(Long accountId, Instant now);
}
