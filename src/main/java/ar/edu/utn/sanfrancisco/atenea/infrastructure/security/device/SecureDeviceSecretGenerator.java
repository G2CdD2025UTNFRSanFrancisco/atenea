package ar.edu.utn.sanfrancisco.atenea.infrastructure.security.device;

import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;
import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceSecretGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class SecureDeviceSecretGenerator implements DeviceSecretGenerator {

    private static final int DEFAULT_BYTES = 32;
    private final SecureRandom random;

    public SecureDeviceSecretGenerator() {
        this.random = new SecureRandom();
    }

    @Override
    public PlainSecret generateSecure() {
        byte[] bytes = new byte[DEFAULT_BYTES];
        random.nextBytes(bytes);
        String encoded = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
        return new PlainSecret(encoded.toCharArray());
    }
}
