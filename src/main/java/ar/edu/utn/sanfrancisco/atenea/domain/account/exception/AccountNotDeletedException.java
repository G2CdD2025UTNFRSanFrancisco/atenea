package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class AccountNotDeletedException extends BusinessException {
    public AccountNotDeletedException() {
        super("This account has never been deleted. It cannot be restored.", "account.not_deleted", 410);
    }
}
