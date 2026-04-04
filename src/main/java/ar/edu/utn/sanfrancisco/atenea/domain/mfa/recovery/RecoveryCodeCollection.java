package ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery;

import java.util.Set;

public final class RecoveryCodeCollection {

    private final Set<HashedRecoveryCode> codes;

    public RecoveryCodeCollection(Set<HashedRecoveryCode> codes) {
        this.codes = codes;
    }

    public void use(HashedRecoveryCode code) {
        HashedRecoveryCode found = codes.stream()
                .filter(c -> c.equals(code) && !c.isUsed())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid recovery code"));
        found.markUsed();
    }

    public Set<HashedRecoveryCode> codes() {
        return Set.copyOf(codes);
    }

}

