package ge.nika.store.ordering.service;

import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.order.model.OrderEvent;
import ge.nika.store.ordering.domain.value.Id;
import ge.nika.store.ordering.event.EventProcessor;
import ge.nika.store.ordering.event.StateStream;
import ge.nika.store.ordering.repository.OrderEventStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderEventsProcessor implements EventProcessor<Order, OrderEvent> {

    private final OrderEventStore orderEventStore;
    private final Sinks.Many<OrderEvent> eventsSink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Flux<Order> streamState(Id orderId) {
        return new StateStream<>(Order.empty(), streamEvents(orderId)).get();
    }

    @Override
    public Flux<OrderEvent> streamEvents(Id orderId) {
        return Flux.create(sink ->
                orderEventStore.findAllByEntityIdOrderByEventCreateTimeAsc(orderId)
                    .concatWith(eventsSink.asFlux().filter(ev -> ev.getEntityId().equals(orderId)))
                    .subscribe(sink::next, sink::error));
    }

    @Override
    public Flux<OrderEvent> streamEvents(Class<? extends OrderEvent> eventType) {
        return Flux.create(sink ->
                orderEventStore.findEventsByClassNameOrderByEventCreateTimeAsc(eventType.getName())
                        .concatWith(eventsSink.asFlux())
                        .filter(event -> event.getClass() == eventType)
                        .subscribe(sink::next, sink::error));
    }

    @Override
    public Mono<Void> publish(OrderEvent event) {
        eventsSink.tryEmitNext(event);
        return Mono.create(sink -> orderEventStore.save(event).subscribe(value -> sink.success()));
    }

    @Override
    public Mono<Order> reconstructStateOn(Id entityId, LocalDateTime time) {
        return orderEventStore
                .findAllByEntityIdAndEventCreateTimeBeforeOrderByEventCreateTimeAsc(entityId, time)
                .reduce(Order.empty(), (order, event) -> event.applyTo(order));
    }
}
