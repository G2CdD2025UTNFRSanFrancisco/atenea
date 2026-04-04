package ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp;

import ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions.InvalidTotpCodeException;

public record TotpCode(String value) {
    public TotpCode {
        if (!value.matches("\\d{6}")) {
            throw new InvalidTotpCodeException();
        }
    }
}
