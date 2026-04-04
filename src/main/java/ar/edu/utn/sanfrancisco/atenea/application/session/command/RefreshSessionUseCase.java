package ar.edu.utn.sanfrancisco.atenea.application.session.command;

import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.RefreshSessionCommand;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.RefreshSessionResponse;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot.AccountSessionSnapshot;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.AccessTokenBuilder;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenSigner;
import ar.edu.utn.sanfrancisco.atenea.domain.session.*;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidRefreshTokenException;

import java.time.Clock;

public class RefreshSessionUseCase {

    private final SessionRepository sessionRepository;
    private final AccountRepository accountRepository;

    private final RefreshTokenHashService refreshTokenHashService;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final TokenSigner tokenSigner;
    private final Clock clock;

    public RefreshSessionUseCase(
            final SessionRepository sessionRepository,
            final AccountRepository accountRepository,
            final RefreshTokenHashService refreshTokenHashService,
            final RefreshTokenGenerator refreshTokenGenerator,
            final TokenSigner tokenSigner,
            final Clock clock
    ) {
        this.sessionRepository = sessionRepository;
        this.accountRepository = accountRepository;
        this.refreshTokenHashService = refreshTokenHashService;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.tokenSigner = tokenSigner;
        this.clock = clock;
    }

    public RefreshSessionResponse execute(final RefreshSessionCommand command) {
        final HashedRefreshToken hashedRefreshToken = this.refreshTokenHashService.hash(command.refreshToken());
        final Session session = this.sessionRepository.findByRefreshTokenAndDeviceId(hashedRefreshToken, command.deviceId())
                .orElseThrow(InvalidRefreshTokenException::new);
        final AccountSessionSnapshot account = this.accountRepository
                .findAccountSessionSnapshotByIdAndVersion(session.getAccountId(), session.getVersionSnapshot())
                .orElseThrow(InvalidRefreshTokenException::new);
        final PlainRefreshToken newRefreshToken = this.refreshTokenGenerator.generate();
        session.rotateRefreshToken(
                newRefreshToken,
                this.refreshTokenHashService,
                this.clock
        );

        final String accessToken = AccessTokenBuilder.forAccess(account.id())
                .scopes(account.scopes())
                .mfaEnabled(account.mfaEnabled())
                .sign(this.tokenSigner, this.clock);
        this.sessionRepository.update(session);
        return new RefreshSessionResponse(
                accessToken,
                new String(newRefreshToken.value())
        );
    }

}
