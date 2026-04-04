package ar.edu.utn.sanfrancisco.atenea.domain.account.credential;

import java.util.Objects;

public record HashedPassword(
        String value
) {

    public HashedPassword {
        Objects.requireNonNull(value);
    }

    @Override
    public String
    toString() {
        return "HashedPassword{" +
                "value='" + value + '\'' +
                '}';
    }
}
