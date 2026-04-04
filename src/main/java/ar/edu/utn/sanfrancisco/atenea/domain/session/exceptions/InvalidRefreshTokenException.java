package ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class InvalidRefreshTokenException extends BusinessException {

    public InvalidRefreshTokenException() {
        super("The provided refresh token is invalid.", "session.invalid_token", 401);
    }
}
