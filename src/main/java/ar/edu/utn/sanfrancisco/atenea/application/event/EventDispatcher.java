package ar.edu.utn.sanfrancisco.atenea.application.event;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.Event;

import java.util.Set;

public interface EventDispatcher {

    void publish(final Event event);

    void publishAll(final Set<Event> events);

}
