package ar.edu.utn.sanfrancisco.atenea.domain.device;

import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;

public interface DeviceSecretGenerator {
    PlainSecret generateSecure();
}
