package ar.edu.utn.sanfrancisco.atenea.infrastructure.identity;

import ar.edu.utn.sanfrancisco.atenea.domain.identity.Identity;
import ar.edu.utn.sanfrancisco.atenea.domain.identity.IdentityGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@Component
public class MonotonicIdentityGenerator implements IdentityGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private final AtomicLong sequence = new AtomicLong(System.currentTimeMillis());

    @Override
    public <V, T extends Identity<V>> T nextLong(final Function<Long, T> constructor) {
        return constructor.apply(sequence.incrementAndGet());
    }

    @Override
    public <V, T extends Identity<V>> T nextString(final Function<String, T> constructor) {
        final byte[] randomBytes = new byte[16];
        RANDOM.nextBytes(randomBytes);
        return constructor.apply(HexFormat.of().formatHex(randomBytes));
    }
}

