package ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.session.SessionId;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

import java.util.Map;

public class SessionNotFoundException extends BusinessException {

    private final SessionId id;

    public SessionNotFoundException(final SessionId id) {
        super("The requested session does not exist.", "session.not_found", 404);
        this.id = id;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("session_id", id);
    }
}
