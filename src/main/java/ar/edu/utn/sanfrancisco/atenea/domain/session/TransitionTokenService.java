package ar.edu.utn.sanfrancisco.atenea.domain.session;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;

import java.util.Optional;

public interface TransitionTokenService {

    String issue(AccountId accountId, DeviceId deviceId, TokenPurpose purpose);

    Optional<AccountId> lookupAccountId(String token, TokenPurpose purpose, DeviceId deviceId);

    void consumeToken(String token, TokenPurpose purpose, DeviceId deviceId);
}

