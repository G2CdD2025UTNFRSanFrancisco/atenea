package ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class InvalidTotpCodeException extends BusinessException {
    public InvalidTotpCodeException() {
        super("The submitted TOTP code is invalid.", "mfa.totp_invalid", 403);
    }
}
