package ar.edu.utn.sanfrancisco.atenea.application.mfa.command;

import ar.edu.utn.sanfrancisco.atenea.domain.account.Account;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollment;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollmentRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions.MfaNotEnrolledException;

public class DisableMfaUseCase {

    private final AccountRepository accountRepository;
    private final MfaEnrollmentRepository mfaRepo;

    public DisableMfaUseCase(
            final AccountRepository accountRepository,
            final MfaEnrollmentRepository mfaRepo
    ) {
        this.accountRepository = accountRepository;
        this.mfaRepo = mfaRepo;
    }

    public void execute(AccountId accountId) {
        Account account = accountRepository.findAccountById(accountId)
                .orElseThrow(IllegalStateException::new);
        MfaEnrollment enrollment = mfaRepo.findByAccountId(accountId)
                .orElseThrow(MfaNotEnrolledException::new);
        enrollment.disable();
        account.setMfaRequired(false);
        accountRepository.update(account);
        mfaRepo.update(enrollment);
    }
}
