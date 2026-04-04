package ar.edu.utn.sanfrancisco.atenea.domain.account.credential;

import lombok.Getter;

import java.time.Instant;

@Getter
public final class Password {

    private final HashedPassword hash;
    private final boolean changeRequired;
    private final Instant lastChangedAt;

    public Password(
            HashedPassword hash,
            boolean changeRequired,
            Instant lastChangedAt
    ) {
        this.hash = hash;
        this.changeRequired = changeRequired;
        this.lastChangedAt = lastChangedAt;
    }

    public Password markChangeRequired() {
        return new Password(hash, true, lastChangedAt);
    }

    public Password changeTo(final HashedPassword newHash, final Instant now) {
        return new Password(newHash, false, now);
    }

    public boolean matches(final PlainPassword password, final PasswordHashService hasher) {
        return hasher.matches(this.hash, password);
    }

}
