package ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class InvalidRecoveryCodeException extends BusinessException {

    public InvalidRecoveryCodeException() {
        super(
                "Invalid recovery code.",
                "mfa.recovery_code.invalid",
                403
        );
    }

}

