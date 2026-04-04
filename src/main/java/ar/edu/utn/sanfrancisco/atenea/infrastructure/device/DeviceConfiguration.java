package ar.edu.utn.sanfrancisco.atenea.infrastructure.device;

import ar.edu.utn.sanfrancisco.atenea.application.device.command.CreateDeviceUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.device.command.DeleteDeviceUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.device.query.GetAllDeviceDetailsUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.device.query.GetDeviceDetailsUseCase;
import ar.edu.utn.sanfrancisco.atenea.domain.identity.IdentityGenerator;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.SecretHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceSecretGenerator;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class DeviceConfiguration {

    @Bean
    public GetDeviceDetailsUseCase provideGetDeviceDetailsUseCase(final DeviceRepository deviceRepository) {
        return new GetDeviceDetailsUseCase(deviceRepository);
    }

    @Bean
    public GetAllDeviceDetailsUseCase provideGetAllDeviceDetailsUseCase(final DeviceRepository deviceRepository) {
        return new GetAllDeviceDetailsUseCase(deviceRepository);
    }

    @Bean
    public CreateDeviceUseCase provideCreateDeviceUseCase(
            final DeviceRepository deviceRepository,
            final ParkingSpotRepository parkingSpotRepository,
            final IdentityGenerator identityGenerator,
            final DeviceSecretGenerator deviceSecretGenerator,
            final SecretHashService secretHashService,
            final Clock clock
    ) {
        return new CreateDeviceUseCase(
                deviceRepository,
                parkingSpotRepository,
                identityGenerator,
                deviceSecretGenerator,
                secretHashService,
                clock
        );
    }

    @Bean
    public DeleteDeviceUseCase provideDeleteDeviceUseCase(
            final DeviceRepository deviceRepository,
            final Clock clock
    ) {
        return new DeleteDeviceUseCase(
              deviceRepository,
              clock
        );
    }
}
