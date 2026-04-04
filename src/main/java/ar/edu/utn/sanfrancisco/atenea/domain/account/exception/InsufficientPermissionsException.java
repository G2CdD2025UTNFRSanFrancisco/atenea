package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scope;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

import java.util.Map;
import java.util.Set;

public class InsufficientPermissionsException extends BusinessException {

    private final Set<Scope> scopes;

    public InsufficientPermissionsException(final Set<Scope> scopes) {
        super("You don't have enough permissions.", "account.scopes.insufficent", 403);
        this.scopes = scopes;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("scopes", this.scopes);
    }
}
