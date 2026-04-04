package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;
import lombok.Getter;

import java.util.Map;

@Getter
public class CannotRevokeOwnAdminException extends BusinessException {

    private final AccountId id;

    public CannotRevokeOwnAdminException(final AccountId id) {
        super("You cannot revoke your own admin permissions", "account.scopes.revoke_unabled", 403);
        this.id = id;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("account_id", id);
    }
}
