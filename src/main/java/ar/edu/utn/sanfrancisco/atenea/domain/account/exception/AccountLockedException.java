package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

import java.time.Instant;
import java.util.Map;

public class AccountLockedException extends BusinessException {
    private final Instant until;
    public AccountLockedException(final Instant until) {
        super("This account is currently locked.", "account.locked", 403);
        this.until = until;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("until", this.until);
    }
}
