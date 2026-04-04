package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;
import lombok.Getter;

import java.util.Map;

@Getter
public class AccountNotFoundException extends BusinessException {
    private final AccountId id;

    public AccountNotFoundException(final AccountId id) {
        super("This account does not exist.", "account.not_found", 404);
        this.id = id;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("account_id", id);
    }
}
