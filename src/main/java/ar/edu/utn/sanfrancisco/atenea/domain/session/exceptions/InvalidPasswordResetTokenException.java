package ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class InvalidPasswordResetTokenException extends BusinessException {

    public InvalidPasswordResetTokenException() {
        super("The provided password reset token is invalid.", "session.invalid_password_reset_token", 401);
    }
}

