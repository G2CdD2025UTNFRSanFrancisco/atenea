package ar.edu.utn.sanfrancisco.atenea.infrastructure.security.account;

import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpCode;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpService;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;

@Slf4j
@Component
public class DefaultTotpService implements TotpService {

    private static final int SECRET_BYTES = 20; // 160-bit
    private static final int TIME_STEP_SECONDS = 30;
    private static final int CODE_DIGITS = 6;
    private static final int WINDOW = 1;

    private static final Base32 BASE32 = new Base32();
    private final SecureRandom random = new SecureRandom();

    @Override
    public PlainSecret generateSecret() {
        byte[] bytes = new byte[SECRET_BYTES];
        random.nextBytes(bytes);
        final var secret = PlainSecret.fromBytes(bytes);
        log.info("Generated {}", secret);
        return secret;
    }

    @Override
    public boolean verify(PlainSecret secret, TotpCode code, Instant at) {
        long timestep = at.getEpochSecond() / TIME_STEP_SECONDS;

        for (int i = -WINDOW; i <= WINDOW; i++) {
            String candidate = generateCode(secret, timestep + i);
            if (constantTimeEquals(candidate, code.value())) {
                return true;
            }
        }
        return false;
    }

    private String generateCode(PlainSecret secret, long timestep) {
        byte[] key = null;
        byte[] hash = null;

        try {
            key = secret.toBytes();

            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putLong(timestep);

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));

            hash = mac.doFinal(buffer.array());

            int offset = hash[hash.length - 1] & 0x0F;

            int binary =
                    ((hash[offset] & 0x7F) << 24) |
                            ((hash[offset + 1] & 0xFF) << 16) |
                            ((hash[offset + 2] & 0xFF) << 8) |
                            (hash[offset + 3] & 0xFF);

            int otp = binary % 1_000_000;

            return zeroPad(otp);

        } catch (Exception e) {
            throw new RuntimeException("Failed generating TOTP", e);
        } finally {
            wipe(key);
            wipe(hash);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    public String buildOtpAuthUri(String issuer, String accountName, PlainSecret secret) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=%d&period=%d",
                urlEncode(issuer),
                urlEncode(accountName),
                secret.toString(),
                urlEncode(issuer),
                CODE_DIGITS,
                TIME_STEP_SECONDS
        );
    }

    private String urlEncode(String value) {
        return value.replace(" ", "%20");
    }

    private String zeroPad(int otp) {
        char[] buf = new char[CODE_DIGITS];
        for (int i = CODE_DIGITS - 1; i >= 0; i--) {
            buf[i] = (char) ('0' + (otp % 10));
            otp /= 10;
        }
        return new String(buf);
    }

    private void wipe(byte[] data) {
        if (data != null) {
            Arrays.fill(data, (byte) 0);
        }
    }
}
