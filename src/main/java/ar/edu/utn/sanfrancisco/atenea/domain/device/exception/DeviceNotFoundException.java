package ar.edu.utn.sanfrancisco.atenea.domain.device.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

import java.util.Map;

public class DeviceNotFoundException extends BusinessException {
    private final DeviceId id;

    public DeviceNotFoundException(final DeviceId id) {
        super("This device does not exist.", "device.not_found", 404);
        this.id = id;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("device_id", id);
    }
}
