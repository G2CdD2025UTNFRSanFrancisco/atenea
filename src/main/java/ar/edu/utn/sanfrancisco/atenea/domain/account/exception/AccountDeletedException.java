package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class AccountDeletedException extends BusinessException {
    public AccountDeletedException() {
        super("This account was deleted.", "account.deleted", 410);
    }
}
