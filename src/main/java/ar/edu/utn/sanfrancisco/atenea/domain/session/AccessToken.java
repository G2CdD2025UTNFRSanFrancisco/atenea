package ar.edu.utn.sanfrancisco.atenea.domain.session;

import java.time.Duration;

public sealed interface AccessToken {

    enum Purpose {
        ACCESS,
        MFA_CHALLENGE,
        PASSWORD_RECOVERY
    }

    String value();
    Duration ttl();
    Purpose purpose();

    record Session(String value, Duration ttl) implements AccessToken {
        @Override
        public Purpose purpose() {
            return Purpose.ACCESS;
        }
    }

    record MfaTransition(String value, Duration ttl) implements AccessToken {
        @Override
        public Purpose purpose() {
            return Purpose.MFA_CHALLENGE;
        }
    }

    record PasswordChangeTransition(String value, Duration ttl) implements AccessToken {
        @Override
        public Purpose purpose() {
            return Purpose.PASSWORD_RECOVERY;
        }
    }

}
