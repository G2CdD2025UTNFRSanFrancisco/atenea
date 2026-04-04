package ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaState;

public class MfaNotEnrolledException extends MfaStateException {

    public MfaNotEnrolledException() {
        super(
                "Multi-factor authentication is not enrolled.",
                "mfa.not_enrolled",
                404,
                MfaState.NOT_ENROLLED
        );
    }

}

