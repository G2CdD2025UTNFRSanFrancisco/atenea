package ar.edu.utn.sanfrancisco.atenea.application.mfa.command;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollment;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollmentRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions.MfaNotEnrolledException;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.*;

import java.util.Set;
import java.util.stream.Collectors;

public class RegenerateRecoveryCodesUseCase {

    private final MfaEnrollmentRepository mfaRepository;
    private final RecoveryCodeGenerator recoveryGenerator;
    private final RecoveryCodeHashService recoveryCodeHashService;

    public RegenerateRecoveryCodesUseCase(
            final MfaEnrollmentRepository mfaRepository,
            final RecoveryCodeGenerator recoveryGenerator,
            final RecoveryCodeHashService recoveryCodeHashService
    ) {
        this.mfaRepository = mfaRepository;
        this.recoveryGenerator = recoveryGenerator;
        this.recoveryCodeHashService = recoveryCodeHashService;
    }

    public PlainRecoveryCollection execute(AccountId accountId) {
        MfaEnrollment enrollment = mfaRepository.findByAccountId(accountId)
                .orElseThrow(MfaNotEnrolledException::new);

        PlainRecoveryCollection plainCodes = recoveryGenerator.generate();
        Set<HashedRecoveryCode> hashedCodes = plainCodes.codes()
                .stream()
                .map(this.recoveryCodeHashService::hash)
                .collect(Collectors.toSet());
        RecoveryCodeCollection codes = new RecoveryCodeCollection(hashedCodes);

        enrollment.regenerateRecoveryCodes(codes);
        mfaRepository.update(enrollment);

        return plainCodes;
    }
}
