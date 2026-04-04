package ar.edu.utn.sanfrancisco.atenea.application.device.query;

import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.device.exception.DeviceNotFoundException;
import ar.edu.utn.sanfrancisco.atenea.domain.device.snapshot.DeviceDetailsSnapshot;

public class GetDeviceDetailsUseCase {

    private final DeviceRepository deviceRepository;

    public GetDeviceDetailsUseCase(final DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public DeviceDetailsSnapshot execute(final DeviceId id) {
        return this.deviceRepository.findDeviceDetailsById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }
}
