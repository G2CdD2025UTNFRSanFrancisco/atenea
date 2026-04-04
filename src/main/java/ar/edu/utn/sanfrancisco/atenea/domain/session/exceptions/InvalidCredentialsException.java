package ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super("The provided credentials are invalid", "session.invalid_credentials", 403);
    }
}
