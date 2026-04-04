package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class AccountAlreadyDeletedException extends BusinessException {
    public AccountAlreadyDeletedException() {
        super("This account has already been deleted", "account.deleted", 410);
    }
}
