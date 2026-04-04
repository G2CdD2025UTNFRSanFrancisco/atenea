package ar.edu.utn.sanfrancisco.atenea.infrastructure.security.account;

import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.HashedPassword;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.PasswordHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.PlainPassword;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Argon2PasswordHashService implements PasswordHashService {

    private final PasswordEncoder encoder;

    public Argon2PasswordHashService() {
        this.encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Override
    public HashedPassword hash(final PlainPassword password) {
        final String encoded = encoder.encode(password);
        return new HashedPassword(encoded);
    }

    @Override
    public boolean matches(final HashedPassword hash, final PlainPassword password) {
        return encoder.matches(password, hash.value());
    }
}
