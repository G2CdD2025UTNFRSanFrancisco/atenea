package ar.edu.utn.sanfrancisco.atenea.infrastructure.account.persistence.credential;

import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.HashedPassword;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.Password;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.time.Instant;

@Embeddable
@Getter
public class PasswordEmbeddable {

    @Column(name = "password_hash", nullable = false)
    private String hash;

    @Column(name = "password_change_required", nullable = false)
    private boolean changeRequired;

    @Column(name = "password_last_changed_at", nullable = false)
    private Instant lastChangedAt;

    protected PasswordEmbeddable() {}

    public PasswordEmbeddable(
            String hash,
            boolean changeRequired,
            Instant lastChangedAt
    ) {
        this.hash = hash;
        this.changeRequired = changeRequired;
        this.lastChangedAt = lastChangedAt;
    }

    public Password toDomain() {
        return new Password(
                new HashedPassword(hash),
                changeRequired,
                lastChangedAt
        );
    }

    public static PasswordEmbeddable fromDomain(Password password) {
        if (password == null) return null;

        return new PasswordEmbeddable(
                password.getHash().value(),
                password.isChangeRequired(),
                password.getLastChangedAt()
        );
    }
}
