package ar.edu.utn.sanfrancisco.atenea.application.device.command;

import ar.edu.utn.sanfrancisco.atenea.application.device.command.dto.DeleteDeviceCommand;
import ar.edu.utn.sanfrancisco.atenea.domain.device.Device;
import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.device.exception.DeviceNotFoundException;

import java.time.Clock;

public class DeleteDeviceUseCase {

    private final DeviceRepository deviceRepository;
    private final Clock clock;

    public DeleteDeviceUseCase(
            final DeviceRepository deviceRepository,
            final Clock clock
    ) {
        this.deviceRepository = deviceRepository;
        this.clock = clock;
    }

    public void execute(final DeleteDeviceCommand command) {
        final Device device = this.deviceRepository.findDeviceById(command.deviceId())
                        .orElseThrow(() -> new DeviceNotFoundException(command.deviceId()));
        device.delete(this.clock);
        this.deviceRepository.update(device);
    }

}
