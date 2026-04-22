package ar.edu.utn.sanfrancisco.atenea.application.mfa.command;

import ar.edu.utn.sanfrancisco.atenea.domain.account.Account;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollment;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollmentRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions.InvalidTotpCodeException;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpCode;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpFactor;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpService;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.*;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.SecretEncryptionService;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidSessionException;
import jakarta.transaction.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public class ActivateTotpUseCase {

    private final MfaEnrollmentRepository mfaRepository;
    private final AccountRepository accountRepository;
    private final TotpService totpService;
    private final RecoveryCodeGenerator recoveryGenerator;
    private final RecoveryCodeHashService recoveryCodeHashService;
    private final SecretEncryptionService secretEncryptionService;
    private final Clock clock;

    public ActivateTotpUseCase(
            MfaEnrollmentRepository mfaRepository,
            AccountRepository accountRepository,
            TotpService totpService,
            RecoveryCodeGenerator recoveryGenerator,
            RecoveryCodeHashService recoveryCodeHashService,
            SecretEncryptionService secretEncryptionService,
            Clock clock
    ) {
        this.mfaRepository = mfaRepository;
        this.accountRepository = accountRepository;
        this.totpService = totpService;
        this.recoveryGenerator = recoveryGenerator;
        this.recoveryCodeHashService = recoveryCodeHashService;
        this.secretEncryptionService = secretEncryptionService;
        this.clock = clock;
    }

    @Transactional
    public PlainRecoveryCollection execute(AccountId accountId, String userTotpCode) {
        final MfaEnrollment enrollment = mfaRepository.findByAccountId(accountId)
                .orElseThrow(InvalidSessionException::new);

        final TotpFactor factor = enrollment.getTotpFactor();
        final PlainSecret secret = this.secretEncryptionService.decrypt(factor.getSecret());

        if (!totpService.verify(secret, new TotpCode(userTotpCode), Instant.now(clock))) {
            throw new InvalidTotpCodeException();
        }

        final Account account = accountRepository.findAccountById(accountId)
                .orElseThrow(IllegalStateException::new); // Atomicidad: Si el enrollment existe, la cuenta debería existir también
        account.setMfaRequired(true);
        PlainRecoveryCollection plainCodes = recoveryGenerator.generate();
        Set<HashedRecoveryCode> hashedCodes = plainCodes.codes()
                .stream()
                .map(this.recoveryCodeHashService::hash)
                .collect(Collectors.toSet());
        RecoveryCodeCollection codes = new RecoveryCodeCollection(hashedCodes);
        enrollment.activateTotp(codes, Instant.now(clock));
        accountRepository.update(account);
        mfaRepository.update(enrollment);
        return plainCodes;
    }
}
