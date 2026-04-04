package ar.edu.utn.sanfrancisco.atenea.application.session.command;

import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.RevokeCurrentSessionCommand;
import ar.edu.utn.sanfrancisco.atenea.domain.session.HashedRefreshToken;
import ar.edu.utn.sanfrancisco.atenea.domain.session.RefreshTokenHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.session.Session;
import ar.edu.utn.sanfrancisco.atenea.domain.session.SessionRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidRefreshTokenException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class RevokeCurrentSessionUseCase {

    private final SessionRepository sessionRepository;
    private final RefreshTokenHashService refreshTokenHashService;
    private final Clock clock;

    public RevokeCurrentSessionUseCase(
            final SessionRepository sessionRepository,
            final RefreshTokenHashService refreshTokenHashService,
            final Clock clock
    ) {
        this.sessionRepository = sessionRepository;
        this.refreshTokenHashService = refreshTokenHashService;
        this.clock = clock;
    }

    @Transactional
    public void execute(final RevokeCurrentSessionCommand command) {
        final HashedRefreshToken hash = this.refreshTokenHashService.hash(command.refreshToken());
        final Session session = this.sessionRepository.findByRefreshTokenAndDeviceId(hash, command.deviceId())
                .orElseThrow(InvalidRefreshTokenException::new);
        session.revoke(this.clock);
        this.sessionRepository.update(session);
    }

}
