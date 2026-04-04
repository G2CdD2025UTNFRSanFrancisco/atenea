package ar.edu.utn.sanfrancisco.atenea.domain.shared;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public abstract class BusinessException extends RuntimeException {

    private final String errorCode;
    private final int statusCode;

    public BusinessException(final String message, final String errorCode, final int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public Map<String, Object> getMetadata() {
        return Collections.emptyMap();
    }

}
