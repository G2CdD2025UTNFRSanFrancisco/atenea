package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;
import lombok.Getter;

@Getter
public class InsufficientHierarchyException extends BusinessException {
    private final AccountId actor;
    private final AccountId subject;

    public InsufficientHierarchyException(final AccountId actor, final AccountId subject) {
        super("This account cannot modify subject's account.", "account.hierarchy.insufficent", 403);
        this.actor = actor;
        this.subject = subject;
    }
}
