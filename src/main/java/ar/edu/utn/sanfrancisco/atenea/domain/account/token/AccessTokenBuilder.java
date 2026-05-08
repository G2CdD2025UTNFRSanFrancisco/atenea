package ar.edu.utn.sanfrancisco.atenea.domain.account.token;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;

import java.time.Clock;

public abstract class AccessTokenBuilder {

    private final AccountId accountId;

    public static DefaultAccessTokenBuilder forAccess(final AccountId accountId) {
        return new DefaultAccessTokenBuilder(accountId);
    }


    protected AccessTokenBuilder(final AccountId accountId) {
        this.accountId = accountId;
    }

    protected AccountId getAccountId() {
        return accountId;
    }

    protected abstract void validate();
    public abstract String sign(final TokenSigner signer, final Clock clock);
}