package ar.edu.utn.sanfrancisco.atenea.infrastructure.security.session;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;
import ar.edu.utn.sanfrancisco.atenea.domain.session.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidMfaChallengeTokenException;
import org.springframework.stereotype.Component;

@Component
public class MfaChallengeTokenDecoder {

    private final OpaqueTransitionTokenService opaqueTransitionTokenService;

    public MfaChallengeTokenDecoder(final OpaqueTransitionTokenService opaqueTransitionTokenService) {
        this.opaqueTransitionTokenService = opaqueTransitionTokenService;
    }

    public AccountId accountIdFrom(final String transitionToken, final DeviceId deviceId) {
        return opaqueTransitionTokenService
                .lookupAccountId(transitionToken, TokenPurpose.MFA_CHALLENGE, deviceId)
                .orElseThrow(InvalidMfaChallengeTokenException::new);
    }

    public void consume(final String transitionToken, final DeviceId deviceId) {
        opaqueTransitionTokenService.consumeToken(transitionToken, TokenPurpose.MFA_CHALLENGE, deviceId);
    }

}

