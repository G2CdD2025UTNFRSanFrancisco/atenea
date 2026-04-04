package ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.recovery;

import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.HashedRecoveryCode;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.PlainRecoveryCode;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.RecoveryCodeHashService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Argon2RecoveryCodeHashService implements RecoveryCodeHashService {

    private final Argon2PasswordEncoder encoder;

    public Argon2RecoveryCodeHashService() {
        this.encoder = new Argon2PasswordEncoder(
                16,
                32,
                1,
                1 << 16,
                4
        );
    }

    @Override
    public HashedRecoveryCode hash(PlainRecoveryCode code) {
        String encoded = encoder.encode(code);
        return new HashedRecoveryCode(encoded);
    }

    @Override
    public boolean matches(HashedRecoveryCode hash, PlainRecoveryCode code) {
        return encoder.matches(code, hash.getValue());
    }
}
