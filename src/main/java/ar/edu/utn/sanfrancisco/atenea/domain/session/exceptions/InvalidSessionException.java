package ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class InvalidSessionException extends BusinessException {
    public InvalidSessionException() {
        super("This session is either revoked or expired.", "session.invalid", 401);
    }
}
