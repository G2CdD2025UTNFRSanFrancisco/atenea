package ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.rabbitmq;

import ar.edu.utn.sanfrancisco.atenea.application.spot.command.UpdateSpotStatusUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.spot.command.dto.UpdateSpotStatusCommand;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.OccupancyStatus;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.exception.ParkingSpotNotFoundException;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.rabbitmq.dto.SpotStatusUpdatedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class SpotStatusMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotStatusMessageListener.class);

    private final UpdateSpotStatusUseCase updateSpotStatusUseCase;

    public SpotStatusMessageListener(final UpdateSpotStatusUseCase updateSpotStatusUseCase) {
        this.updateSpotStatusUseCase = updateSpotStatusUseCase;
    }

    @RabbitListener(queues = "${atenea.rabbitmq.spot-status.queue}")
    public void onStatusUpdated(final SpotStatusUpdatedMessage message) {
        try {
            final UpdateSpotStatusCommand command = new UpdateSpotStatusCommand(
                    new ParkingSpotId(requireText(message.spotId(), "spot_id")),
                    parseStatus(message.status())
            );
            this.updateSpotStatusUseCase.execute(command);
        } catch (ParkingSpotNotFoundException | IllegalArgumentException ex) {
            LOGGER.warn("Discarding invalid spot status update message: {}", message, ex);
        }
    }

    private OccupancyStatus parseStatus(final String rawStatus) {
        return OccupancyStatus.valueOf(requireText(rawStatus, "status").toUpperCase(Locale.ROOT));
    }

    private String requireText(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing field: " + fieldName);
        }
        return value.trim();
    }
}

