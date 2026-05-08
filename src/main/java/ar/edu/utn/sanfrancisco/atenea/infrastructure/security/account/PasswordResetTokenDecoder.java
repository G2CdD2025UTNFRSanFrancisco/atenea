package ar.edu.utn.sanfrancisco.atenea.infrastructure.security.account;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;
import ar.edu.utn.sanfrancisco.atenea.domain.session.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidPasswordResetTokenException;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.security.session.OpaqueTransitionTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PasswordResetTokenDecoder {

    private final OpaqueTransitionTokenService opaqueTransitionTokenService;

    public PasswordResetTokenDecoder(final OpaqueTransitionTokenService opaqueTransitionTokenService) {
        this.opaqueTransitionTokenService = opaqueTransitionTokenService;
    }

    public AccountId accountIdFrom(final String transitionToken, final DeviceId deviceId) {
        try {
            return opaqueTransitionTokenService
                    .lookupAccountId(transitionToken, TokenPurpose.PASSWORD_RESET, deviceId)
                    .orElseThrow(InvalidPasswordResetTokenException::new);
        } catch (final InvalidPasswordResetTokenException e) {
            log.info("Transition Token {} doesn't exist in the db.", transitionToken);
            throw e;
        }
    }

    public void consume(final String transitionToken, final DeviceId deviceId) {
        opaqueTransitionTokenService.consumeToken(transitionToken, TokenPurpose.PASSWORD_RESET, deviceId);
    }

}


