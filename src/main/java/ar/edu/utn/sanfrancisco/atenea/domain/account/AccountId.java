package ar.edu.utn.sanfrancisco.atenea.domain.account;

import ar.edu.utn.sanfrancisco.atenea.domain.identity.Identity;

import java.util.Objects;

public record AccountId(
        Long value
) implements Identity<Long> {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AccountId accountId = (AccountId) o;
        return value == accountId.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
