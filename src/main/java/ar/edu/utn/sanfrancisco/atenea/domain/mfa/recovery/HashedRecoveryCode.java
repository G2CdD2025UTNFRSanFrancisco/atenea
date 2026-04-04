package ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery;

import lombok.Getter;

@Getter
public final class HashedRecoveryCode{

    private final String value;
    private boolean used;

    public HashedRecoveryCode(String value) {
        this.value = value;
        this.used = false;
    }

    public HashedRecoveryCode(final String value, final boolean used) {
        this.value = value;
        this.used = used;
    }

    public void markUsed() {
        this.used = true;
    }

}

