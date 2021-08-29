package ge.nika.store.ordering.event;

import ge.nika.store.ordering.domain.value.Id;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface EventProcessor<ENTITY, EVENT extends Event<ENTITY>> {
    Flux<ENTITY> streamState(Id entityId);
    Flux<EVENT> streamEvents(Id entityId);
    Flux<EVENT> streamEvents(Class<? extends EVENT> eventType);
    Mono<Void> publish(EVENT event);
    Mono<ENTITY> reconstructStateOn(Id entityId, LocalDateTime time);
}
