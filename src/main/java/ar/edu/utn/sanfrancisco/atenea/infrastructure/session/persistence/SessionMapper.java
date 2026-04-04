package ar.edu.utn.sanfrancisco.atenea.infrastructure.session.persistence;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.SessionVersion;
import ar.edu.utn.sanfrancisco.atenea.domain.session.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.session.HashedRefreshToken;
import ar.edu.utn.sanfrancisco.atenea.domain.session.Session;
import ar.edu.utn.sanfrancisco.atenea.domain.session.SessionId;

public final class SessionMapper {
    public static SessionEntity toEntity(Session domain) {
        return new SessionEntity(
                domain.getId().value(),
                domain.getAccountId().value(),
                domain.getDeviceId().value(),
                domain.getVersionSnapshot().value(),
                domain.getRefreshToken().value(),
                domain.getCreatedAt(),
                domain.getExpiresAt(),
                domain.getRevokedAt()
        );
    }

    public static Session toDomain(SessionEntity entity) {
        return Session.reconstitute(
                new SessionId(entity.getId()),
                new AccountId(entity.getAccountId()),
                new DeviceId(entity.getDeviceId()),
                new SessionVersion(entity.getVersionSnapshot()),
                new HashedRefreshToken(entity.getRefreshToken()),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getRevokedAt()
        );
    }

}
