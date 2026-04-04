package ar.edu.utn.sanfrancisco.atenea.domain.mfa.exceptions;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class TotpAlreadyEnrolledException extends BusinessException {

    public TotpAlreadyEnrolledException() {
        super(
                "A TOTP factor is already enrolled.",
                "mfa.totp_already_enrolled",
                409
        );
    }

}
