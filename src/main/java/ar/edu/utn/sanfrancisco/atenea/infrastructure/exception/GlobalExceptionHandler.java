package ar.edu.utn.sanfrancisco.atenea.infrastructure.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(final MethodArgumentNotValidException ex) {
        final ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setDetail("Request validation failed.");
        problem.setProperty("error_code", "VALIDATION_ERROR");

        final Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        if (!errors.isEmpty()) {
            problem.setProperty("metadata", errors);
        }
        return problem;
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ProblemDetail handleBadRequest(final RuntimeException ex) {
        final ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setDetail("Invalid request format.");
        problem.setProperty("error_code", "INVALID_REQUEST");
        return problem;
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ProblemDetail handleUnauthorized(final AuthorizationDeniedException ex) {
        final ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setDetail("You do not have permission to perform this action.");
        problem.setProperty("error_code", "ACCESS_DENIED");
        return problem;
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(final BusinessException ex) {
        final ProblemDetail problem = ProblemDetail.forStatus(ex.getStatusCode());
        problem.setDetail(ex.getMessage());
        problem.setProperty("error_code", ex.getErrorCode());
        if (!ex.getMetadata().isEmpty()) {
            problem.setProperty("metadata", ex.getMetadata());
        }
        return problem;
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(final RuntimeException ex) {
        final ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setDetail("An unexpected error occurred. Try again later.");
        problem.setProperty("error_code", "INTERNAL_ERROR");
        log.error("an unexpected error occurred: ", ex);
        return problem;
    }

}
