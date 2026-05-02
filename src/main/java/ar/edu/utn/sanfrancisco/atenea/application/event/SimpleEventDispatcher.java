package ar.edu.utn.sanfrancisco.atenea.application.event;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.Event;

import java.util.*;

public class SimpleEventDispatcher implements EventDispatcher {

    private final Map<Class<?>, List<EventHandler<?>>> handlers;

    public SimpleEventDispatcher(List<EventHandler<?>> handlers) {
        this.handlers = new HashMap<>();

        for (EventHandler<?> handler : handlers) {
            this.handlers
                    .computeIfAbsent(handler.eventType(), k -> new ArrayList<>())
                    .add(handler);
        }
    }

    @Override
    public void publish(Event event) {
        final List<EventHandler<?>> eventHandlers =
                handlers.getOrDefault(event.getClass(), List.of());

        for (final EventHandler handler : eventHandlers) {
            handler.handle(event);
        }
    }

    @Override
    public void publishAll(Set<Event> events) {
        events.forEach(this::publish);
    }
}
