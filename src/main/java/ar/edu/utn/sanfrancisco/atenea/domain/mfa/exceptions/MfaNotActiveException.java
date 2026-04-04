package ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaState;

public class MfaNotActiveException extends MfaStateException {

    public MfaNotActiveException(MfaState state) {
        super(
                "Multi-factor authentication is not active.",
                "mfa.not_active",
                403,
                state
        );
    }

}

