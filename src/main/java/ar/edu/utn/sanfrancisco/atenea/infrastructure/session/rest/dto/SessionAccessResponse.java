package ar.edu.utn.sanfrancisco.atenea.infrastructure.session.rest.dto;

public record SessionAccessResponse(
        Status status,
        String accessToken,
        Long accountId
) {
    public enum Status {
        SUCCESS,
        MFA_REQUIRED,
        PASSWORD_CHANGE_REQUIRED
    }
}

