package ar.edu.utn.sanfrancisco.atenea.infrastructure.session.persistence;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;
import ar.edu.utn.sanfrancisco.atenea.domain.session.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.session.HashedRefreshToken;
import ar.edu.utn.sanfrancisco.atenea.domain.session.TransitionToken;
import ar.edu.utn.sanfrancisco.atenea.domain.session.TransitionTokenId;

public final class TransitionTokenMapper {

    private TransitionTokenMapper() {
    }

    public static TransitionTokenEntity toEntity(final TransitionToken domain) {
        return new TransitionTokenEntity(
                domain.getId().value(),
                domain.getAccountId().value(),
                domain.getDeviceId().value(),
                domain.getPurpose(),
                domain.getTokenHash().value(),
                domain.getCreatedAt(),
                domain.getExpiresAt(),
                domain.getConsumedAt(),
                domain.getRevokedAt()
        );
    }

    public static TransitionToken toDomain(final TransitionTokenEntity entity) {
        return TransitionToken.reconstitute(
                new TransitionTokenId(entity.getId()),
                new AccountId(entity.getAccountId()),
                new DeviceId(entity.getDeviceId()),
                entity.getPurpose(),
                new HashedRefreshToken(entity.getTokenHash()),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getConsumedAt(),
                entity.getRevokedAt()
        );
    }
}


