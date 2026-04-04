package ar.edu.utn.sanfrancisco.atenea.application.mfa.command;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollment;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollmentRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions.MfaNotEnrolledException;

public class DisableMfaUseCase {

    private final MfaEnrollmentRepository mfaRepo;

    public DisableMfaUseCase(final MfaEnrollmentRepository mfaRepo) {
        this.mfaRepo = mfaRepo;
    }

    public void execute(AccountId accountId) {
        MfaEnrollment enrollment = mfaRepo.findByAccountId(accountId)
                .orElseThrow(MfaNotEnrolledException::new);
        enrollment.disable();
        mfaRepo.update(enrollment);
    }
}
