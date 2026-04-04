package ar.edu.utn.sanfrancisco.atenea.application.spot.command;

import ar.edu.utn.sanfrancisco.atenea.application.event.EventDispatcher;
import ar.edu.utn.sanfrancisco.atenea.application.spot.command.dto.UpdateSpotStatusCommand;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpot;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.event.ParkingSpotStatusUpdatedEvent;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.exception.ParkingSpotNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Slf4j
public class UpdateSpotStatusUseCase {

    private final ParkingSpotRepository parkingSpotRepository;
    private final EventDispatcher eventDispatcher;
    private final Clock clock;

    public UpdateSpotStatusUseCase(
            final ParkingSpotRepository parkingSpotRepository,
            final EventDispatcher eventDispatcher,
            final Clock clock
    ) {
        this.parkingSpotRepository = parkingSpotRepository;
        this.eventDispatcher = eventDispatcher;
        this.clock = clock;
    }

    @Transactional
    public void execute(final UpdateSpotStatusCommand command) {
        final ParkingSpot parkingSpot = parkingSpotRepository.findParkingSpotById(command.id())
                .orElseThrow(() -> new ParkingSpotNotFoundException(command.id()));
        final ParkingSpotStatusUpdatedEvent event = parkingSpot.update(command.status(), this.clock);
        if (event == null) return;
        this.parkingSpotRepository.update(parkingSpot);
        eventDispatcher.publish(event);
    }

}
