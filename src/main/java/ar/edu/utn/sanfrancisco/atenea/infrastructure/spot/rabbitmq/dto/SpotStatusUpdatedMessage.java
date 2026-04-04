package ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record SpotStatusUpdatedMessage(
        @JsonAlias("spot_id")
        String spotId,
        String status
) {
}

