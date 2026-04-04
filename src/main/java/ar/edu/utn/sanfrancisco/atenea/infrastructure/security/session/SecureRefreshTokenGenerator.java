package ar.edu.utn.sanfrancisco.atenea.infrastructure.security.session;

import ar.edu.utn.sanfrancisco.atenea.domain.session.PlainRefreshToken;
import ar.edu.utn.sanfrancisco.atenea.domain.session.RefreshTokenGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public class SecureRefreshTokenGenerator implements RefreshTokenGenerator {

    private static final int TOKEN_BYTE_LENGTH = 32; // 256 bits
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public PlainRefreshToken generate() {
        byte[] bytes = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(bytes);
        char[] token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes)
                .toCharArray();
        Arrays.fill(bytes, (byte) 0);
        return new PlainRefreshToken(token);
    }
}