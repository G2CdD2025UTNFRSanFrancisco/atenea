package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

import java.util.Map;

public class InsufficientPermissionsException extends BusinessException {

    private final String requiredPermission;

    public InsufficientPermissionsException(final String requiredPermission) {
        super("You don't have enough permissions.", "account.permissions.insufficient", 403);
        this.requiredPermission = requiredPermission;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("required", this.requiredPermission);
    }
}
