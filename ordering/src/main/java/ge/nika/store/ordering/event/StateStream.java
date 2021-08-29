package ge.nika.store.ordering.event;

import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicReference;

public class StateStream<ENTITY, EVENT extends Event<ENTITY>> {

    private final AtomicReference<ENTITY> currentState;
    private final Flux<EVENT> eventStream;

    public StateStream(ENTITY currentState, Flux<EVENT> eventStream) {
        this.currentState = new AtomicReference<>(currentState);
        this.eventStream = eventStream;
    }

    public Flux<ENTITY> get() {
        return eventStream.doOnNext(event -> {
            ENTITY newState = event.applyTo(currentState.get());
            currentState.compareAndSet(currentState.get(), newState);
        }).map(event -> currentState.get());
    }
}
