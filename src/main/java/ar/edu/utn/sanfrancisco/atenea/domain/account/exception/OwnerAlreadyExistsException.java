package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class OwnerAlreadyExistsException extends BusinessException {

    public OwnerAlreadyExistsException() {
        super("There can only be one owner account.", "account.owner.exists", 409);
    }
}

