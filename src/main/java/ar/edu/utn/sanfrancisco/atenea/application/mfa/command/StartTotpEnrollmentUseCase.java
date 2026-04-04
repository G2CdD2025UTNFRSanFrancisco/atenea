package ar.edu.utn.sanfrancisco.atenea.application.mfa.command;

import ar.edu.utn.sanfrancisco.atenea.domain.account.Account;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.AccountNotFoundException;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollment;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollmentRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaPolicy;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpFactor;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpService;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.SecretEncryptionService;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

public class StartTotpEnrollmentUseCase {

    private final MfaEnrollmentRepository mfaRepository;
    private final AccountRepository accountRepository;
    private final TotpService totpService;
    private final SecretEncryptionService secretEncryptionService;
    private final Clock clock;

    public StartTotpEnrollmentUseCase(
            MfaEnrollmentRepository mfaRepository,
            AccountRepository accountRepository,
            TotpService totpService,
            SecretEncryptionService secretEncryptionService,
            Clock clock
    ) {
        this.mfaRepository = mfaRepository;
        this.accountRepository = accountRepository;
        this.totpService = totpService;
        this.secretEncryptionService = secretEncryptionService;
        this.clock = clock;
    }

    @Transactional
    public StartTotpEnrollmentResult execute(AccountId accountId) {
        Account account = accountRepository.findAccountById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        Optional<MfaEnrollment> optional = mfaRepository.findByAccountId(accountId);

        MfaEnrollment enrollment;
        boolean isNew = optional.isEmpty();

        final String issuer = "Atenea";
        final String accountLabel = account.getUsername().value();

        if (isNew) {
            enrollment = new MfaEnrollment(accountId, MfaPolicy.defaultPolicy());
        } else {
            enrollment = optional.get();
            final TotpFactor factor = enrollment.getTotpFactor();
            final PlainSecret secret = this.secretEncryptionService.decrypt(factor.getSecret());
            final String otpauthUri = totpService.buildOtpAuthUri(issuer, accountLabel, secret);
            return new StartTotpEnrollmentResult(secret, otpauthUri, issuer, accountLabel);
        }

        PlainSecret secret = totpService.generateSecret();

        enrollment.startTotpEnrollment(
                secret,
                issuer,
                accountLabel,
                Instant.now(clock),
                this.secretEncryptionService
        );

        mfaRepository.create(enrollment);

        final String otpauthUri = totpService.buildOtpAuthUri(issuer, accountLabel, secret);
        return new StartTotpEnrollmentResult(secret, otpauthUri, issuer, accountLabel);
    }
}

