package ar.edu.utn.sanfrancisco.atenea.domain.mfa;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;

import java.util.Optional;

public interface MfaEnrollmentRepository {
    Optional<MfaEnrollment> findByAccountId(AccountId id);

    void create(MfaEnrollment mfa);
    void update(MfaEnrollment mfa);
}
