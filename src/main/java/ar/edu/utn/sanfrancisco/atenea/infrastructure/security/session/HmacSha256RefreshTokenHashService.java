package ar.edu.utn.sanfrancisco.atenea.infrastructure.security.session;

import ar.edu.utn.sanfrancisco.atenea.domain.session.HashedRefreshToken;
import ar.edu.utn.sanfrancisco.atenea.domain.session.PlainRefreshToken;
import ar.edu.utn.sanfrancisco.atenea.domain.session.RefreshTokenHashService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HexFormat;

@Service
public class HmacSha256RefreshTokenHashService implements RefreshTokenHashService {

    private static final String ALGORITHM = "HmacSHA256";
    private final byte[] secret;

    public HmacSha256RefreshTokenHashService(@Value("${atenea.security.refresh-token.hmac-secret}") String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public HashedRefreshToken hash(PlainRefreshToken plain) {
        byte[] tokenBytes = null;

        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(secret, ALGORITHM));

            tokenBytes = StandardCharsets.UTF_8.encode(
                    CharBuffer.wrap(plain.value())
            ).array();

            byte[] hashBytes = mac.doFinal(tokenBytes);

            String hashed = HexFormat.of().formatHex(hashBytes);

            return new HashedRefreshToken(hashed);

        } catch (Exception e) {
            throw new IllegalStateException("Error hashing refresh token", e);

        } finally {
            if (tokenBytes != null) {
                Arrays.fill(tokenBytes, (byte) 0);
            }
        }
    }
}
