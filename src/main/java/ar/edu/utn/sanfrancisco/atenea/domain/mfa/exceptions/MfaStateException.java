package ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaState;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

import java.util.Map;

public abstract class MfaStateException extends BusinessException {

    private final MfaState state;

    protected MfaStateException(
            String message,
            String code,
            int status,
            MfaState state
    ) {
        super(message, code, status);
        this.state = state;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("state", state);
    }
}
