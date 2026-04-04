package ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.websocket;

import ar.edu.utn.sanfrancisco.atenea.domain.spot.OccupancyStatus;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.event.ParkingSpotStatusUpdatedEvent;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.websocket.dto.ParkingSpotStatusUpdatedWsMessage;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ParkingSpotStatusUpdatedWebSocketConsumerTest {

    @Test
    void shouldExposeHandledEventType() {
        final SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        final ParkingSpotStatusUpdatedWebSocketConsumer consumer =
                new ParkingSpotStatusUpdatedWebSocketConsumer(messagingTemplate, "/topic/spots/status");

        assertEquals(ParkingSpotStatusUpdatedEvent.class, consumer.eventType());
    }

    @Test
    void shouldPublishEventToConfiguredDestination() {
        final SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        final ParkingSpotStatusUpdatedWebSocketConsumer consumer =
                new ParkingSpotStatusUpdatedWebSocketConsumer(messagingTemplate, "/topic/spots/status");

        final Instant occurredAt = Instant.parse("2026-03-26T15:15:30Z");
        final ParkingSpotStatusUpdatedEvent event = new ParkingSpotStatusUpdatedEvent(
                new ParkingSpotId("A1"),
                OccupancyStatus.OCCUPIED,
                occurredAt
        );
        final ParkingSpotStatusUpdatedWsMessage expectedPayload =
                new ParkingSpotStatusUpdatedWsMessage("A1", OccupancyStatus.OCCUPIED, occurredAt);

        consumer.handle(event);

        verify(messagingTemplate).convertAndSend("/topic/spots/status", expectedPayload);
    }
}

