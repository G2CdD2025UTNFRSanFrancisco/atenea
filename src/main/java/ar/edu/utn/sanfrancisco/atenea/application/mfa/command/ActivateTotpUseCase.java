package ar.edu.utn.sanfrancisco.atenea.application.mfa.command;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollment;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollmentRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions.InvalidTotpCodeException;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpCode;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpFactor;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpService;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.*;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.SecretEncryptionService;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public class ActivateTotpUseCase {

    private final MfaEnrollmentRepository mfaRepository;
    private final TotpService totpService;
    private final RecoveryCodeGenerator recoveryGenerator;
    private final RecoveryCodeHashService recoveryCodeHashService;
    private final SecretEncryptionService secretEncryptionService;
    private final Clock clock;

    public ActivateTotpUseCase(
            MfaEnrollmentRepository mfaRepository,
            TotpService totpService,
            RecoveryCodeGenerator recoveryGenerator,
            RecoveryCodeHashService recoveryCodeHashService,
            SecretEncryptionService secretEncryptionService,
            Clock clock
    ) {
        this.mfaRepository = mfaRepository;
        this.totpService = totpService;
        this.recoveryGenerator = recoveryGenerator;
        this.recoveryCodeHashService = recoveryCodeHashService;
        this.secretEncryptionService = secretEncryptionService;
        this.clock = clock;
    }

    public PlainRecoveryCollection execute(AccountId accountId, String userTotpCode) {
        final MfaEnrollment enrollment = mfaRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalStateException("No MFA enrollment"));

        final TotpFactor factor = enrollment.getTotpFactor();
        final PlainSecret secret = this.secretEncryptionService.decrypt(factor.getSecret());

        if (!totpService.verify(secret, new TotpCode(userTotpCode), Instant.now(clock))) {
            throw new InvalidTotpCodeException();
        }

        PlainRecoveryCollection plainCodes = recoveryGenerator.generate();
        Set<HashedRecoveryCode> hashedCodes = plainCodes.codes()
                .stream()
                .map(this.recoveryCodeHashService::hash)
                .collect(Collectors.toSet());
        RecoveryCodeCollection codes = new RecoveryCodeCollection(hashedCodes);
        enrollment.activateTotp(codes, Instant.now(clock));
        mfaRepository.update(enrollment);
        return plainCodes;
    }
}
