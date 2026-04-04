package ar.edu.utn.sanfrancisco.atenea.infrastructure;

import ar.edu.utn.sanfrancisco.atenea.application.event.EventDispatcher;
import ar.edu.utn.sanfrancisco.atenea.application.event.EventHandler;
import ar.edu.utn.sanfrancisco.atenea.application.event.SimpleEventDispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import java.time.Clock;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class AteneaConfiguration {

    @Bean
    public Clock provideClock() {
        return Clock.systemUTC();
    }

    @Bean
    public EventDispatcher provideEventDispatcher(final List<EventHandler<?>> handlers) {
        return new SimpleEventDispatcher(handlers);
    }
}
