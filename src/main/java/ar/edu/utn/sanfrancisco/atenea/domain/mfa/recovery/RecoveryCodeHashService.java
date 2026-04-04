package ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery;

public interface RecoveryCodeHashService {

    HashedRecoveryCode hash(PlainRecoveryCode plain);
    boolean matches(HashedRecoveryCode hash, PlainRecoveryCode code);
}

