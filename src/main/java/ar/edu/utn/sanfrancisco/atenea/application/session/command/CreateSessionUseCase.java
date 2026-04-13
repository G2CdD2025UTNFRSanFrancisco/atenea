package ar.edu.utn.sanfrancisco.atenea.application.session.command;

import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.LoginCommand;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.LoginResponse;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Account;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.authentication.AuthenticationResult;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.PasswordHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.PlainPassword;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.AccountLockedException;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.AccessTokenBuilder;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenSigner;
import ar.edu.utn.sanfrancisco.atenea.domain.identity.IdentityGenerator;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollment;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollmentRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.session.*;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidCredentialsException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

public class CreateSessionUseCase {

    private final AccountRepository accountRepository;
    private final MfaEnrollmentRepository mfaRepository;
    private final SessionRepository sessionRepository;
    private final IdentityGenerator identityGenerator;
    private final PasswordHashService passwordHashService;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final RefreshTokenHashService refreshTokenHashService;
    private final TokenSigner tokenSigner;
    private final Clock clock;

    public CreateSessionUseCase(
            final AccountRepository accountRepository,
            final MfaEnrollmentRepository mfaRepository,
            final SessionRepository sessionRepository,
            final IdentityGenerator identityGenerator,
            final PasswordHashService passwordHashService,
            final RefreshTokenGenerator refreshTokenGenerator,
            final RefreshTokenHashService refreshTokenHashService,
            final TokenSigner tokenSigner,
            final Clock clock
    ) {
        this.accountRepository = accountRepository;
        this.mfaRepository = mfaRepository;
        this.sessionRepository = sessionRepository;
        this.identityGenerator = identityGenerator;
        this.passwordHashService = passwordHashService;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.refreshTokenHashService = refreshTokenHashService;
        this.tokenSigner = tokenSigner;
        this.clock = clock;
    }

    @Transactional
    public LoginResponse execute(LoginCommand command) {

        final Account account = accountRepository
                .findAccountByUsername(command.username())
                .orElseThrow(InvalidCredentialsException::new);

        final AuthenticationResult result;
        try (PlainPassword plain = new PlainPassword(command.password())) {
            result = account.authenticatePassword(plain, passwordHashService, clock);
        }

        accountRepository.update(account);

        return switch (result) {
            case AuthenticationResult.PasswordVerified s -> {

                final Instant now = clock.instant();

                if (requiresMfa(account.getId(), now)) {
                    final String token = AccessTokenBuilder
                            .forMfa(account.getId())
                            .sign(tokenSigner, clock);
                    yield LoginResponse.mfaRequired(token, account.getId());
                }

                final PlainRefreshToken refreshToken = refreshTokenGenerator.generate();
                    final Optional<Session> optional = this.sessionRepository.findByAccountIdAndDeviceId(
                            account.getId(),
                            command.deviceId()
                    );

                    if (optional.isEmpty() || !optional.get().isActive(clock)) {
                        final Session session = Session.create(
                                account,
                                command.deviceId(),
                                refreshToken,
                                this.identityGenerator,
                                this.refreshTokenHashService,
                                this.clock
                        );
                        this.sessionRepository.create(session);
                    } else {
                        final Session session = optional.get();
                        session.rotateRefreshToken(
                                refreshToken,
                                this.refreshTokenHashService,
                                this.clock
                        );
                        this.sessionRepository.update(session);
                    }

                    final String accessToken = AccessTokenBuilder
                            .forAccess(account.getId())
                            .scopes(account.getScopes())
                            .mfaEnabled(account.requiresMfa())
                            .sign(tokenSigner, clock);

                    yield LoginResponse.success(
                            accessToken,
                            new String(refreshToken.value()),
                            account.getId()
                    );
                }
            case AuthenticationResult.PasswordChangeRequired p -> {
                if (requiresMfa(account.getId(), Instant.now(this.clock))) {
                    final String token = AccessTokenBuilder
                            .forMfa(account.getId())
                            .sign(tokenSigner, clock);
                    yield LoginResponse.mfaRequired(token, account.getId());
                }

                final String token = AccessTokenBuilder
                        .forPasswordReset(account.getId())
                        .sign(tokenSigner, clock);
                yield LoginResponse.passwordChangeRequired(token, account.getId());
            }
            case AuthenticationResult.Locked l ->
                    throw new AccountLockedException(l.until());
            case AuthenticationResult.Deleted d ->
                    throw new InvalidCredentialsException();
            case AuthenticationResult.InvalidCredentials i ->
                    throw new InvalidCredentialsException();
            default ->
                    throw new IllegalStateException("Unexpected value: " + result);
        };
    }

    private boolean requiresMfa(final AccountId accountId, final Instant now) {
        final MfaEnrollment enrollment = mfaRepository
                .findByAccountId(accountId)
                .orElse(null);
        return enrollment != null && enrollment.requiresVerification(now);
    }
}