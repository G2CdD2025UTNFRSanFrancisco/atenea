package ar.edu.utn.sanfrancisco.atenea.domain.session;

import java.util.Objects;

public record DeviceId(
        String value
) {

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DeviceId deviceId = (DeviceId) o;
        return Objects.equals(value, deviceId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
