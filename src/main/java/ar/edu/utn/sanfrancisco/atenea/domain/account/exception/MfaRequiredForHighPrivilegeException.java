package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

import java.util.Map;

public class MfaRequiredForHighPrivilegeException extends BusinessException {

    private final String role;

    public MfaRequiredForHighPrivilegeException(final String role) {
        super("The account needs mfa enabled for this role.", "account.mfa.required", 403);
        this.role = role;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("role", this.role);
    }
}
