package ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery;

import java.util.Set;

public record PlainRecoveryCollection(Set<PlainRecoveryCode> codes) implements AutoCloseable {
    @Override
    public void close() throws Exception {
         codes.forEach(PlainRecoveryCode::close);
    }
}
