package ge.nika.store.ordering.event;

import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.value.Id;
import ge.nika.store.ordering.snapshot.Snapshot;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface EventProcessor<ENTITY, EVENT extends Event<ENTITY>> {
    Flux<ENTITY> streamState(Id entityId);
    Flux<EVENT> streamEvents(Id entityId);
    Flux<EVENT> streamEvents(Class<? extends EVENT> eventType);
    Mono<Void> publish(EVENT event);
    Mono<ENTITY> reconstructState(Id entityId, LocalDateTime time);
    Mono<ENTITY> reconstructStateUsingSnapshot(Id entityId, Snapshot<ENTITY> snapshot, LocalDateTime time);
}
