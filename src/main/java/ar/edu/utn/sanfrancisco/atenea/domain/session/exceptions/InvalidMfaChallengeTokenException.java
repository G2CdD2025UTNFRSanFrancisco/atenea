package ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class InvalidMfaChallengeTokenException extends BusinessException {

    public InvalidMfaChallengeTokenException() {
        super("The provided MFA challenge token is invalid.", "session.invalid_mfa_token", 401);
    }
}

