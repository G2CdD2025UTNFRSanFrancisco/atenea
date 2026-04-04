package ar.edu.utn.sanfrancisco.atenea.domain.device.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;

public class DeviceAlreadyDeletedException extends BusinessException {
    public DeviceAlreadyDeletedException() {
        super("This device has already been deleted", "device.deleted", 410);
    }
}
