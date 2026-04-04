package ar.edu.utn.sanfrancisco.atenea.domain.account.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.account.Username;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;
import lombok.Getter;

import java.util.Map;

@Getter
public class AccountAlreadyExistsException extends BusinessException {

    private final Username username;

    public AccountAlreadyExistsException(final Username username) {
        super("This username is taken", "account.username_taken", 409);
        this.username = username;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("username", username);
    }
}
