package ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.websocket;

import ar.edu.utn.sanfrancisco.atenea.application.event.EventHandler;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.event.ParkingSpotStatusUpdatedEvent;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.websocket.dto.ParkingSpotStatusUpdatedWsMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ParkingSpotStatusUpdatedWebSocketConsumer implements EventHandler<ParkingSpotStatusUpdatedEvent> {

    private final SimpMessagingTemplate messagingTemplate;
    private final String destination;

    public ParkingSpotStatusUpdatedWebSocketConsumer(
            final SimpMessagingTemplate messagingTemplate,
            @Value("${atenea.websocket.destination.spot-status:/topic/spots/status}") final String destination
    ) {
        this.messagingTemplate = messagingTemplate;
        this.destination = destination;
    }

    @Override
    public Class<ParkingSpotStatusUpdatedEvent> eventType() {
        return ParkingSpotStatusUpdatedEvent.class;
    }

    @Override
    public void handle(final ParkingSpotStatusUpdatedEvent event) {
        this.messagingTemplate.convertAndSend(this.destination, ParkingSpotStatusUpdatedWsMessage.fromEvent(event));
    }
}

