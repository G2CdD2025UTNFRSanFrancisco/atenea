package ar.edu.utn.sanfrancisco.atenea.domain.mfa;

import java.time.Duration;

public record MfaPolicy(
        int maxFailures,
        Duration lockDuration
) {
    public static MfaPolicy defaultPolicy() {
        return new MfaPolicy(
                5,
                Duration.ofMinutes(15)
        );
    }
}
