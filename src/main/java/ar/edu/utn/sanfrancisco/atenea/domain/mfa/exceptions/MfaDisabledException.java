package ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaState;

public class MfaDisabledException extends MfaStateException {

    public MfaDisabledException() {
        super(
                "Multi-factor authentication is disabled.",
                "mfa.disabled",
                403,
                MfaState.DISABLED
        );
    }

}

