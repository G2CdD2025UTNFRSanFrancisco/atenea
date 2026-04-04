package ar.edu.utn.sanfrancisco.atenea.infrastructure.security.secret;

import ar.edu.utn.sanfrancisco.atenea.domain.secret.EncryptedSecret;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.SecretEncryptionService;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public final class AesGcmSecretEncryptionService implements SecretEncryptionService {

    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int IV_SIZE = 12;
    private static final int TAG_BITS = 128;

    private final SecretKey key;
    private final SecureRandom random = new SecureRandom();

    public AesGcmSecretEncryptionService(final SecretKey aesSecretKey) {
        this.key = aesSecretKey;
    }

    @Override
    public EncryptedSecret encrypt(final PlainSecret secret) {
        byte[] iv = new byte[IV_SIZE];
        byte[] plain = null;
        byte[] cipherText = null;
        byte[] combined = null;

        try {
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));

            plain = secret.toBytes();
            cipherText = cipher.doFinal(plain);

            combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return new EncryptedSecret(
                    Base64.getEncoder().encodeToString(combined)
            );
        } catch (Exception e) {
            throw new RuntimeException("Secret encryption failed", e);
        } finally {
            wipe(iv);
            wipe(plain);
            wipe(cipherText);
            wipe(combined);
        }
    }

    @Override
    public PlainSecret decrypt(final EncryptedSecret cipherSecret) {
        byte[] combined = null;
        byte[] iv = null;
        byte[] cipherText = null;
        byte[] plain = null;

        try {
            combined = Base64.getDecoder().decode(cipherSecret.value());

            iv = new byte[IV_SIZE];
            cipherText = new byte[combined.length - IV_SIZE];

            System.arraycopy(combined, 0, iv, 0, IV_SIZE);
            System.arraycopy(combined, IV_SIZE, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));

            plain = cipher.doFinal(cipherText);

            return PlainSecret.fromBytes(plain);
        } catch (Exception e) {
            throw new RuntimeException("Secret decryption failed", e);
        } finally {
            wipe(combined);
            wipe(iv);
            wipe(cipherText);
            wipe(plain);
        }
    }

    private void wipe(byte[] data) {
        if (data != null) {
            Arrays.fill(data, (byte) 0);
        }
    }

    public static SecretKey generateKey() {
        try {
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(256);
            return gen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
