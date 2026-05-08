package ar.edu.utn.sanfrancisco.atenea.infrastructure.session;

import ar.edu.utn.sanfrancisco.atenea.application.session.command.CompleteTotpMfaUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.CreateSessionUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.RefreshSessionUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.RevokeAllSessionsUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.RevokeCurrentSessionUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.RevokeSessionUseCase;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.PasswordHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenSigner;
import ar.edu.utn.sanfrancisco.atenea.domain.identity.IdentityGenerator;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollmentRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpService;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.SecretEncryptionService;
import ar.edu.utn.sanfrancisco.atenea.domain.session.RefreshTokenGenerator;
import ar.edu.utn.sanfrancisco.atenea.domain.session.RefreshTokenHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.session.TransitionTokenService;
import ar.edu.utn.sanfrancisco.atenea.domain.session.SessionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class SessionConfiguration {

    @Bean
    public CreateSessionUseCase provideCreateSessionUseCase(
            final AccountRepository accountRepository,
            final MfaEnrollmentRepository mfaEnrollmentRepository,
            final SessionRepository sessionRepository,
            final IdentityGenerator identityGenerator,
            final PasswordHashService passwordHashService,
            final RefreshTokenGenerator refreshTokenGenerator,
            final RefreshTokenHashService refreshTokenHashService,
            final TokenSigner tokenSigner,
            final TransitionTokenService transitionTokenService,
            final Clock clock
    ) {
        return new CreateSessionUseCase(
                accountRepository,
                mfaEnrollmentRepository,
                sessionRepository,
                identityGenerator,
                passwordHashService,
                refreshTokenGenerator,
                refreshTokenHashService,
                tokenSigner,
                transitionTokenService,
                clock
        );
    }

    @Bean
    public CompleteTotpMfaUseCase provideCompleteTotpMfaUseCase(
            final MfaEnrollmentRepository mfaEnrollmentRepository,
            final AccountRepository accountRepository,
            final SessionRepository sessionRepository,
            final IdentityGenerator identityGenerator,
            final TokenSigner tokenSigner,
            final TotpService totpService,
            final SecretEncryptionService secretEncryptionService,
            final RefreshTokenGenerator refreshTokenGenerator,
            final RefreshTokenHashService refreshTokenHashService,
            final TransitionTokenService transitionTokenService,
            final Clock clock
    ) {
        return new CompleteTotpMfaUseCase(
                mfaEnrollmentRepository,
                accountRepository,
                sessionRepository,
                identityGenerator,
                tokenSigner,
                totpService,
                secretEncryptionService,
                refreshTokenGenerator,
                refreshTokenHashService,
                transitionTokenService,
                clock
        );
    }

    @Bean
    public RefreshSessionUseCase provideRefreshSessionUseCase(
            final SessionRepository sessionRepository,
            final AccountRepository accountRepository,
            final RefreshTokenHashService refreshTokenHashService,
            final RefreshTokenGenerator refreshTokenGenerator,
            final TokenSigner tokenSigner,
            final Clock clock
    ) {
        return new RefreshSessionUseCase(
                sessionRepository,
                accountRepository,
                refreshTokenHashService,
                refreshTokenGenerator,
                tokenSigner,
                clock
        );
    }

    @Bean
    public RevokeCurrentSessionUseCase provideRevokeCurrentSessionUseCase(
            final SessionRepository sessionRepository,
            final RefreshTokenHashService refreshTokenHashService,
            final Clock clock
    ) {
        return new RevokeCurrentSessionUseCase(sessionRepository, refreshTokenHashService, clock);
    }

    @Bean
    public RevokeSessionUseCase provideRevokeSessionUseCase(
            final AccountRepository accountRepository,
            final SessionRepository sessionRepository,
            final Clock clock
    ) {
        return new RevokeSessionUseCase(accountRepository, sessionRepository, clock);
    }

    @Bean
    public RevokeAllSessionsUseCase provideRevokeAllSessionsUseCase(
            final AccountRepository accountRepository,
            final SessionRepository sessionRepository,
            final Clock clock
    ) {
        return new RevokeAllSessionsUseCase(accountRepository, sessionRepository, clock);
    }
}

