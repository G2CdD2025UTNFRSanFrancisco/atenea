package ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.recovery;

import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.PlainRecoveryCode;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.PlainRecoveryCollection;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.RecoveryCodeGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Component
public class DefaultRecoveryCodeGenerator implements RecoveryCodeGenerator {

    private static final char[] ALPHABET =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private static final int CODE_LENGTH = 12;
    private static final int CODE_COUNT = 10;

    private final SecureRandom random = new SecureRandom();

    @Override
    public PlainRecoveryCollection generate() {
        Set<PlainRecoveryCode> codes = new HashSet<>(CODE_COUNT);

        for (int i = 0; i < CODE_COUNT; i++) {
            char[] value = new char[CODE_LENGTH];

            for (int j = 0; j < CODE_LENGTH; j++) {
                value[j] = ALPHABET[random.nextInt(ALPHABET.length)];
            }

            codes.add(new PlainRecoveryCode(value));
        }

        return new PlainRecoveryCollection(codes);
    }
}

