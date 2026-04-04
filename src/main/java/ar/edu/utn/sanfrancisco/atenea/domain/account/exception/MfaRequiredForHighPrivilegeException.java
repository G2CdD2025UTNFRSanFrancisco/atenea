package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scope;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

import java.util.Map;
import java.util.Set;

public class MfaRequiredForHighPrivilegeException extends BusinessException {

    private final Set<Scope> scopes;

    public MfaRequiredForHighPrivilegeException(final Set<Scope> scopes) {
        super("The account needs mfa to be granted with this permissions.", "account.mfa.required", 403);
        this.scopes = scopes;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("scopes", this.scopes);
    }
}
