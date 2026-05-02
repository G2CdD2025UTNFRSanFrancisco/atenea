package ar.edu.utn.sanfrancisco.atenea.application.device.command;

import ar.edu.utn.sanfrancisco.atenea.application.device.command.dto.CreateDeviceCommand;
import ar.edu.utn.sanfrancisco.atenea.application.device.command.dto.CreateDeviceResult;
import ar.edu.utn.sanfrancisco.atenea.domain.device.Device;
import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceSecretGenerator;
import ar.edu.utn.sanfrancisco.atenea.domain.identity.IdentityGenerator;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.PlainSecret;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.SecretHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.exception.ParkingSpotAlreadyAssignedException;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.exception.ParkingSpotNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class CreateDeviceUseCase {

    private final DeviceRepository deviceRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final IdentityGenerator identityGenerator;
    private final DeviceSecretGenerator deviceSecretGenerator;
    private final SecretHashService secretHashService;
    private final Clock clock;

    public CreateDeviceUseCase(
            final DeviceRepository deviceRepository,
            final ParkingSpotRepository parkingSpotRepository,
            final IdentityGenerator identityGenerator,
            final DeviceSecretGenerator deviceSecretGenerator,
            final SecretHashService secretHashService,
            final Clock clock
    ) {
        this.deviceRepository = deviceRepository;
        this.parkingSpotRepository = parkingSpotRepository;
        this.identityGenerator = identityGenerator;
        this.deviceSecretGenerator = deviceSecretGenerator;
        this.secretHashService = secretHashService;
        this.clock = clock;
    }

    @Transactional
    public CreateDeviceResult execute(final CreateDeviceCommand command) {
        if (deviceRepository.existsByParkingSpotId(command.spotId()))
            throw new ParkingSpotAlreadyAssignedException(command.spotId());
        if (!parkingSpotRepository.existsById(command.spotId()))
            throw new ParkingSpotNotFoundException(command.spotId());

        final PlainSecret secret = this.deviceSecretGenerator.generateSecure();
        final Device device = Device.create(
                command.spotId(),
                secret,
                this.identityGenerator,
                this.secretHashService,
                this.clock
        );

        this.deviceRepository.create(device);
        return new CreateDeviceResult(
                device.getId(),
                secret
        );
    }

}
