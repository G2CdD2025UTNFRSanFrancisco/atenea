package ar.edu.utn.sanfrancisco.atenea.domain.account.credential.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class InsecurePasswordException extends BusinessException {

    public InsecurePasswordException() {
        super("The password submitted is not secure.", "account.password.insecure", 400);
    }
}
