package ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.persistence.recovery;

import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.HashedRecoveryCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class RecoveryCodeEmbeddable {

    @Column(name = "code_hash", nullable = false)
    private String codeHash;

    @Column(name = "used", nullable = false)
    private boolean used;

    protected RecoveryCodeEmbeddable() {
    }

    public RecoveryCodeEmbeddable(final String codeHash, final boolean used) {
        this.codeHash = codeHash;
        this.used = used;
    }

    public HashedRecoveryCode toDomain() {
        return new HashedRecoveryCode(codeHash, used);
    }

    public static RecoveryCodeEmbeddable fromDomain(final HashedRecoveryCode code) {
        return new RecoveryCodeEmbeddable(code.getValue(), code.isUsed());
    }
}

