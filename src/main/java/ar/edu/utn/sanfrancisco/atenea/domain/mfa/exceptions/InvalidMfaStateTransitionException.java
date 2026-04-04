package ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaState;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

import java.util.Map;

public class InvalidMfaStateTransitionException extends BusinessException {

    private final MfaState current;
    private final String operation;

    public InvalidMfaStateTransitionException(MfaState current, String operation) {
        super(
                "Invalid MFA state transition.",
                "mfa.invalid_state_transition",
                409
        );
        this.current = current;
        this.operation = operation;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of(
                "currentState", current,
                "operation", operation
        );
    }

}

