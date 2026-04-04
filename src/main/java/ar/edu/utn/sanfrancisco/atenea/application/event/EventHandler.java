package ar.edu.utn.sanfrancisco.atenea.application.event;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.Event;

public interface EventHandler<T extends Event> {
    Class<T> eventType();
    void handle(T event);
}
