package ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaState;

public class MfaAlreadyActiveException extends MfaStateException {

    public MfaAlreadyActiveException() {
        super(
                "Multi-factor authentication is already active.",
                "mfa.already_active",
                409,
                MfaState.ACTIVE
        );
    }

}

