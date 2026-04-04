package ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.rabbitmq;

import ar.edu.utn.sanfrancisco.atenea.application.spot.command.UpdateSpotStatusUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.spot.command.dto.UpdateSpotStatusCommand;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.OccupancyStatus;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.exception.ParkingSpotNotFoundException;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.rabbitmq.dto.SpotStatusUpdatedMessage;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class SpotStatusMessageListenerTest {

    @Test
    void shouldDispatchUseCaseWhenMessageIsValid() {
        final UpdateSpotStatusUseCase useCase = mock(UpdateSpotStatusUseCase.class);
        final SpotStatusMessageListener listener = new SpotStatusMessageListener(useCase);

        listener.onStatusUpdated(new SpotStatusUpdatedMessage("A1", "occupied"));

        verify(useCase).execute(new UpdateSpotStatusCommand(new ParkingSpotId("A1"), OccupancyStatus.OCCUPIED));
    }

    @Test
    void shouldDiscardMessageWhenStatusIsInvalid() {
        final UpdateSpotStatusUseCase useCase = mock(UpdateSpotStatusUseCase.class);
        final SpotStatusMessageListener listener = new SpotStatusMessageListener(useCase);

        listener.onStatusUpdated(new SpotStatusUpdatedMessage("A1", "does_not_exist"));

        verifyNoInteractions(useCase);
    }

    @Test
    void shouldDiscardMessageWhenSpotDoesNotExist() {
        final UpdateSpotStatusUseCase useCase = mock(UpdateSpotStatusUseCase.class);
        final SpotStatusMessageListener listener = new SpotStatusMessageListener(useCase);
        doThrow(new ParkingSpotNotFoundException(new ParkingSpotId("A1")))
                .when(useCase)
                .execute(new UpdateSpotStatusCommand(new ParkingSpotId("A1"), OccupancyStatus.FREE));

        listener.onStatusUpdated(new SpotStatusUpdatedMessage("A1", "free"));

        verify(useCase).execute(new UpdateSpotStatusCommand(new ParkingSpotId("A1"), OccupancyStatus.FREE));
    }
}

