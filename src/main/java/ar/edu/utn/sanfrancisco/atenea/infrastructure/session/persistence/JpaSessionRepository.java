package ar.edu.utn.sanfrancisco.atenea.infrastructure.session.persistence;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.session.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaSessionRepository implements SessionRepository {

    private final SpringDataSessionRepository repository;
    private final EntityManager em;
    private final Clock clock;

    @Override
    public Optional<Session> findByAccountIdAndDeviceId(AccountId accountId, DeviceId deviceId) {
        return repository.findByAccountIdAndDeviceIdAndRevokedAtIsNull(
                accountId.value(),
                deviceId.value()
        ).map(SessionMapper::toDomain);
    }

    @Override
    public Optional<Session> findByRefreshTokenAndDeviceId(HashedRefreshToken refreshToken, DeviceId deviceId) {
        return repository
                .findByRefreshTokenAndDeviceIdAndRevokedAtIsNull(
                        refreshToken.value(),
                        deviceId.value()
                ).map(SessionMapper::toDomain);
    }

    @Override
    public Optional<Session> findById(SessionId id) {
        return repository
                .findByIdAndRevokedAtIsNull(id.value())
                .map(SessionMapper::toDomain);
    }

    @Override
    @Transactional
    public void create(Session session) {
        SessionEntity entity = SessionMapper.toEntity(session);
        em.persist(entity);
    }

    @Override
    @Transactional
    public void update(Session session) {
        SessionEntity entity = SessionMapper.toEntity(session);
        em.merge(entity);
    }

    @Override
    @Transactional
    public void revokeAllByAccountId(AccountId accountId) {
        repository.revokeAllByAccountId(
                accountId.value(),
                Instant.now(this.clock)
        );
    }
}
