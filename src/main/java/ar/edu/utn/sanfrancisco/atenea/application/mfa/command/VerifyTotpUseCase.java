package ar.edu.utn.sanfrancisco.atenea.application.mfa.command;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollment;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollmentRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions.InvalidTotpCodeException;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpCode;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpFactor;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpService;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.SecretEncryptionService;

import java.time.Clock;
import java.time.Instant;

public class VerifyTotpUseCase {

    private final MfaEnrollmentRepository mfaRepository;
    private final TotpService totpService;
    private final SecretEncryptionService secretEncryptionService;
    private final Clock clock;

    public VerifyTotpUseCase(
            final MfaEnrollmentRepository mfaRepository,
            final TotpService totpService,
            final SecretEncryptionService secretEncryptionService,
            final Clock clock
    ) {
        this.mfaRepository = mfaRepository;
        this.totpService = totpService;
        this.secretEncryptionService = secretEncryptionService;
        this.clock = clock;
    }

    public void execute(AccountId accountId, TotpCode code) {
        MfaEnrollment enrollment = mfaRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalStateException("No MFA enrollment"));

        final TotpFactor factor = enrollment.getTotpFactor();
        final PlainSecret secret = this.secretEncryptionService.decrypt(factor.getSecret());

        final Instant now = Instant.now(clock);

        if (!totpService.verify(
                secret,
                code,
                now
        )) {
            enrollment.verifyFailure(now);
            mfaRepository.update(enrollment);
            throw new InvalidTotpCodeException();
        }

        enrollment.verifySuccess(now);
        mfaRepository.update(enrollment);
    }
}
