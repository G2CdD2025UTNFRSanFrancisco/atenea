package ar.edu.utn.sanfrancisco.atenea.domain.session;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;

import java.util.Optional;

public interface SessionRepository {

    Optional<Session> findByAccountIdAndDeviceId(final AccountId accountId, final DeviceId deviceId);
    Optional<Session> findByRefreshTokenAndDeviceId(final HashedRefreshToken refreshToken, final DeviceId deviceId);
    Optional<Session> findById(final SessionId id);

    void create(final Session session);
    void update(final Session session);

    void revokeAllByAccountId(final AccountId accountId);

}