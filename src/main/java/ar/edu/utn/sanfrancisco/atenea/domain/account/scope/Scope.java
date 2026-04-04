package ar.edu.utn.sanfrancisco.atenea.domain.account.scope;

import lombok.Getter;

@Getter
public enum Scope {
    READ_ACCOUNTS(AssuranceLevel.LOW),
    MANAGE_ACCOUNTS(AssuranceLevel.HIGH),
    READ_SESSIONS(AssuranceLevel.LOW),
    MANAGE_SESSIONS(AssuranceLevel.HIGH),
    READ_SENSORS(AssuranceLevel.LOW),
    MANAGE_SENSORS(AssuranceLevel.HIGH),
    ADMIN(AssuranceLevel.HIGH);

    private final AssuranceLevel requiredLevel;

    Scope(final AssuranceLevel requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public boolean requiresHighAssurance() {
        return this.requiredLevel == AssuranceLevel.HIGH;
    }
}
