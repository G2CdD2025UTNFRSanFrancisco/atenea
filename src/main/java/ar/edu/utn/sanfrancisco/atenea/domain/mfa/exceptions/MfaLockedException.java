package ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

import java.time.Instant;
import java.util.Map;

public class MfaLockedException extends BusinessException {

    private final Instant until;

    public MfaLockedException(Instant until) {
        super(
                "Multi-factor authentication is temporarily locked.",
                "mfa.locked",
                403
        );
        this.until = until;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("until", until);
    }

}

