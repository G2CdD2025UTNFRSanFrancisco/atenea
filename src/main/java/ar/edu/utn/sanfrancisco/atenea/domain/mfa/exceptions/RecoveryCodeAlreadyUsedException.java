package ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class RecoveryCodeAlreadyUsedException extends BusinessException {

    public RecoveryCodeAlreadyUsedException() {
        super(
                "Recovery code has already been used.",
                "mfa.recovery_code.used",
                409
        );
    }

}

