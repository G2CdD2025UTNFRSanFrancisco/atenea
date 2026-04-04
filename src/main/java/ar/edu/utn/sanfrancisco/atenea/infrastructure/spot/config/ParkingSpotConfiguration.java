package ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.config;

import ar.edu.utn.sanfrancisco.atenea.application.event.EventDispatcher;
import ar.edu.utn.sanfrancisco.atenea.application.event.EventHandler;
import ar.edu.utn.sanfrancisco.atenea.application.event.SimpleEventDispatcher;
import ar.edu.utn.sanfrancisco.atenea.application.spot.command.UpdateSpotStatusUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.spot.query.GetAllParkingSpotsUseCase;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotRepository;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
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

    @Bean
    public Queue spotStatusQueue(@Value("${atenea.rabbitmq.spot-status.queue}") final String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public DirectExchange spotStatusExchange(@Value("${atenea.rabbitmq.spot-status.exchange}") final String exchangeName) {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Binding spotStatusBinding(
            final Queue spotStatusQueue,
            final DirectExchange spotStatusExchange,
            @Value("${atenea.rabbitmq.spot-status.routing-key}") final String routingKey
    ) {
        return BindingBuilder.bind(spotStatusQueue).to(spotStatusExchange).with(routingKey);
    }

}
