package ar.edu.utn.sanfrancisco.atenea.domain.account.role;

import lombok.Getter;

@Getter
public enum Role {
    OWNER(AssuranceLevel.HIGH),
    ADMIN(AssuranceLevel.HIGH),
    USER(AssuranceLevel.LOW);

    private final AssuranceLevel requiredLevel;

    Role(final AssuranceLevel requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public boolean requiresHighAssurance() {
        return this.requiredLevel == AssuranceLevel.HIGH;
    }

    public boolean canManageAccounts() {
        return this == OWNER || this == ADMIN;
    }

    public boolean canOperateOn(final Role target) {
        return switch (this) {
            case OWNER -> true;
            case ADMIN -> target == USER;
            case USER -> false;
        };
    }
}
