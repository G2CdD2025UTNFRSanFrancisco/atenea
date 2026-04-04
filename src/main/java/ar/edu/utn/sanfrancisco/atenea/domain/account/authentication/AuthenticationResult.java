package ar.edu.utn.sanfrancisco.atenea.domain.account.authentication;

import java.time.Instant;

public sealed interface AuthenticationResult permits AuthenticationResult.Deleted, AuthenticationResult.InvalidCredentials, AuthenticationResult.InvalidTotp, AuthenticationResult.Locked, AuthenticationResult.MfaRequired, AuthenticationResult.PasswordChangeRequired, AuthenticationResult.PasswordVerified
{
    record PasswordVerified() implements AuthenticationResult { }
    record InvalidCredentials() implements AuthenticationResult { }
    record Locked(Instant until) implements AuthenticationResult { }
    record PasswordChangeRequired() implements AuthenticationResult { }
    record Deleted() implements AuthenticationResult { }
    record MfaRequired() implements AuthenticationResult { }
    record InvalidTotp() implements AuthenticationResult { }
}
