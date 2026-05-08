package ar.edu.utn.sanfrancisco.atenea.application.session.command;

import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.LoginResponse;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Account;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.AccountNotFoundException;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.AccessTokenBuilder;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenSigner;
import ar.edu.utn.sanfrancisco.atenea.domain.identity.IdentityGenerator;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollment;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollmentRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions.MfaNotEnrolledException;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpCode;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpService;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.SecretEncryptionService;
import ar.edu.utn.sanfrancisco.atenea.domain.session.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Optional;

public class CompleteTotpMfaUseCase {

    private final MfaEnrollmentRepository mfaRepository;
    private final AccountRepository accountRepository;
    private final SessionRepository sessionRepository;
    private final IdentityGenerator identityGenerator;
    private final TokenSigner tokenSigner;
    private final TotpService totpService;
    private final SecretEncryptionService encryptionService;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final RefreshTokenHashService refreshTokenHashService;
    private final TransitionTokenService transitionTokenService;
    private final Clock clock;

    public CompleteTotpMfaUseCase(
            MfaEnrollmentRepository mfaRepository,
            AccountRepository accountRepository,
            SessionRepository sessionRepository,
            IdentityGenerator identityGenerator,
            TokenSigner tokenSigner,
            TotpService totpService,
            SecretEncryptionService encryptionService,
            RefreshTokenGenerator refreshTokenGenerator,
            RefreshTokenHashService refreshTokenHashService,
            TransitionTokenService transitionTokenService,
            Clock clock
    ) {
        this.mfaRepository = mfaRepository;
        this.accountRepository = accountRepository;
        this.sessionRepository = sessionRepository;
        this.identityGenerator = identityGenerator;
        this.tokenSigner = tokenSigner;
        this.totpService = totpService;
        this.encryptionService = encryptionService;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.refreshTokenHashService = refreshTokenHashService;
        this.transitionTokenService = transitionTokenService;
        this.clock = clock;
    }

    @Transactional
    public LoginResponse execute(AccountId accountId, DeviceId deviceId, TotpCode totpCode) {
        final Account account = this.accountRepository.findAccountById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.canAuthenticate(this.clock);

        MfaEnrollment mfa = this.mfaRepository.findByAccountId(accountId)
                .orElseThrow(MfaNotEnrolledException::new);
        mfa.verifyTotp(totpCode, this.totpService, this.encryptionService, this.clock);

        if (account.requiresChangePassword()) {
            final String token = transitionTokenService.issue(
                    account.getId(),
                    deviceId,
                    TokenPurpose.PASSWORD_RESET
            );
            return LoginResponse.passwordChangeRequired(
                    token,
                    accountId
            );
        }

        PlainRefreshToken refreshToken = refreshTokenGenerator.generate();
        final Optional<Session> optional = this.sessionRepository.findByAccountIdAndDeviceId(
                accountId,
                deviceId
        );
        if (optional.isEmpty() || !optional.get().isActive(clock)) {
            final Session session = Session.create(
                    account,
                    deviceId,
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
        final String accessToken = AccessTokenBuilder.forAccess(account.getId())
                .role(account.getRole())
                .mfaEnabled(account.requiresMfa())
                .sign(tokenSigner, clock);
        accountRepository.update(account);
        return LoginResponse.success(accessToken, new String(refreshToken.value()), account.getId());
    }
}
