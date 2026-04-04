package ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.config;

import ar.edu.utn.sanfrancisco.atenea.application.event.EventDispatcher;
import ar.edu.utn.sanfrancisco.atenea.application.event.EventHandler;
import ar.edu.utn.sanfrancisco.atenea.application.event.SimpleEventDispatcher;
import ar.edu.utn.sanfrancisco.atenea.application.spot.command.UpdateSpotStatusUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.spot.query.GetAllParkingSpotsUseCase;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;

@Configuration
public class ParkingSpotConfiguration {

    @Bean
    public GetAllParkingSpotsUseCase provideGetAllParkingSpotsUseCase(final ParkingSpotRepository repository) {
        return new GetAllParkingSpotsUseCase(repository);
    }

    @Bean
    public UpdateSpotStatusUseCase provideUpdateSpotStatusUseCase(
            final ParkingSpotRepository parkingSpotRepository,
            final EventDispatcher eventDispatcher,
            final Clock clock
    ) {
        return new UpdateSpotStatusUseCase(parkingSpotRepository, eventDispatcher, clock);
    }

}
