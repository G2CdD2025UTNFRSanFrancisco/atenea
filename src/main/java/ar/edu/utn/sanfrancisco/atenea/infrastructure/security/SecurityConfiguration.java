package ar.edu.utn.sanfrancisco.atenea.infrastructure.security;

import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.security.jwt.TokenPurposeJwtDecoderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfiguration {

    private static final int MIN_SECRET_BYTES = 32;
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Bean
    public SecretKey tokenSecretKey(@Value("${atenea.security.secret.hmac-secret}") String tokenSecret) {
        final byte[] keyBytes = tokenSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalArgumentException("security.token.hmac-secret must have at least 32 bytes");
        }
        return new SecretKeySpec(keyBytes, HMAC_ALGORITHM);
    }

    @Bean
    public SecretKey aesSecretKey(@Value("${atenea.security.secret.aes-secret}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("AES key must be 16, 24 or 32 bytes");
        }

        return new SecretKeySpec(keyBytes, "AES");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey tokenSecretKey) {
        return NimbusJwtEncoder.withSecretKey(tokenSecretKey).build();
    }

    @Bean
    public JwtDecoder jwtDecoder(TokenPurposeJwtDecoderFactory tokenPurposeJwtDecoderFactory) {
        return tokenPurposeJwtDecoderFactory.create(TokenPurpose.ACCESS);
    }
}
