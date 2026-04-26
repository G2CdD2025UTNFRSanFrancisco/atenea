package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

import java.util.Map;

public class CannotDisableMfaWithHighPrivilegesException extends BusinessException {

    private final String role;

    public CannotDisableMfaWithHighPrivilegesException(final String role) {
        super("You cannot disable mfa with high privilege roles.", "account.mfa.required", 403);
        this.role = role;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("role", this.role);
    }
}
