package ar.edu.utn.sanfrancisco.atenea.infrastructure.security.secret;

import ar.edu.utn.sanfrancisco.atenea.domain.secret.HashedSecret;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.SecretHashService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public final class BCryptSecretHashService implements SecretHashService {

    private final PasswordEncoder encoder;

    public BCryptSecretHashService() {
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public HashedSecret hash(final PlainSecret secret) {
        final String hashed = encoder.encode(secret);
        return new HashedSecret(hashed);
    }

    @Override
    public boolean matches(final HashedSecret hash, final PlainSecret secret) {
        return encoder.matches(secret, hash.value());
    }
}

