package ar.edu.utn.sanfrancisco.atenea.domain.session;

import ar.edu.utn.sanfrancisco.atenea.domain.identity.Identity;

public record SessionId(Long value) implements Identity<Long> {
}
