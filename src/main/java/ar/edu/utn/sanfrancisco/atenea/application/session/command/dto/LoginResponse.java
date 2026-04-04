package ar.edu.utn.sanfrancisco.atenea.application.session.command.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;

public record LoginResponse(
        Status status,
        String accessToken,
        String refreshToken,
        AccountId accountId
) {
    public enum Status {
        SUCCESS,
        MFA_REQUIRED,
        PASSWORD_CHANGE_REQUIRED
    }

    public static LoginResponse success(String token, String refreshToken, AccountId accountId) {
        return new LoginResponse(Status.SUCCESS, token, refreshToken, accountId);
    }

    public static LoginResponse mfaRequired(String transitionToken, AccountId accountId) {
        return new LoginResponse(Status.MFA_REQUIRED, transitionToken, null, accountId);
    }

    public static LoginResponse passwordChangeRequired(String transitionToken, AccountId accountId) {
        return new LoginResponse(Status.PASSWORD_CHANGE_REQUIRED, transitionToken, null, accountId);
    }
}
