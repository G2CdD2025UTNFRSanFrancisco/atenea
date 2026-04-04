package ar.edu.utn.sanfrancisco.atenea.domain.device;

import ar.edu.utn.sanfrancisco.atenea.domain.identity.Identity;

public record DeviceId(String value) implements Identity<String> {
}
