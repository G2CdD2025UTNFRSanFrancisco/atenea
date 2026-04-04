package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scope;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

import java.util.Map;
import java.util.Set;

public class CannotDisableMfaWithHighPrivilegesException extends BusinessException {

    private final Set<Scope> scopes;

    public CannotDisableMfaWithHighPrivilegesException(final Set<Scope> scopes) {
        super("You cannot disable mfa with high privileges.", "account.mfa.required", 403);
        this.scopes = scopes;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("scopes", this.scopes);
    }
}
