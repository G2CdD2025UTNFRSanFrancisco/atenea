package ar.edu.utn.sanfrancisco.atenea.infrastructure.session.persistence;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;
import ar.edu.utn.sanfrancisco.atenea.domain.session.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.session.HashedRefreshToken;
import ar.edu.utn.sanfrancisco.atenea.domain.session.TransitionToken;
import ar.edu.utn.sanfrancisco.atenea.domain.session.TransitionTokenRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaTransitionTokenRepository implements TransitionTokenRepository {

    private final SpringDataTransitionTokenRepository repository;
    private final EntityManager em;

    @Override
    public Optional<TransitionToken> findActiveByHashAndPurposeAndDeviceId(
            final HashedRefreshToken tokenHash,
            final TokenPurpose purpose,
            final DeviceId deviceId,
            final Instant now
    ) {
        return repository
                .findFirstByTokenHashAndPurposeAndDeviceIdAndConsumedAtIsNullAndRevokedAtIsNullAndExpiresAtAfter(
                        tokenHash.value(),
                        purpose,
                        deviceId.value(),
                        now
                )
                .map(TransitionTokenMapper::toDomain);
    }

    @Override
    @Transactional
    public void create(final TransitionToken token) {
        em.persist(TransitionTokenMapper.toEntity(token));
    }

    @Override
    @Transactional
    public void update(final TransitionToken token) {
        em.merge(TransitionTokenMapper.toEntity(token));
    }

    @Override
    @Transactional
    public void revokeAllByAccountId(final AccountId accountId) {
        // Intentionally left for future bulk revocation policies.
    }
}

