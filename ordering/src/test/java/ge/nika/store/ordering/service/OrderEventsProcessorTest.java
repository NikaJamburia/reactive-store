package ge.nika.store.ordering.service;

import ge.nika.store.ordering.domain.order.event.OrderCreated;
import ge.nika.store.ordering.domain.order.event.OrderProcessed;
import ge.nika.store.ordering.domain.order.event.OrderShipped;
import ge.nika.store.ordering.domain.order.event.OrderSubmitted;
import ge.nika.store.ordering.domain.order.model.OrderEvent;
import ge.nika.store.ordering.domain.order.value.LineItem;
import ge.nika.store.ordering.domain.value.Id;
import ge.nika.store.ordering.repository.OrderEventStore;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Set;

import static ge.nika.store.ordering.domain.order.model.OrderStatus.*;
import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.parse;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderEventsProcessorTest {

    private final Id orderId = Id.random();
    private final Sinks.Many<OrderEvent> defaultSink = Sinks.many().multicast().onBackpressureBuffer();

    @Test
    public void streamsStateOfAnOrder() {

        OrderEventStore eventStore = mock(OrderEventStore.class);
        when(eventStore.findAllByEntityIdOrderByEventCreateTimeAsc(orderId)).thenReturn(Flux.just(
                new OrderCreated(randomUUID(), orderId, now(), Set.of(mock(LineItem.class))),
                new OrderSubmitted(randomUUID(), orderId, now()),
                new OrderProcessed(randomUUID(), orderId, now()),
                new OrderShipped(randomUUID(), orderId, now())
        ));

        StepVerifier.create(new OrderEventsProcessor(eventStore, defaultSink).streamState(orderId))
                .assertNext(order -> {
                    assertEquals(orderId, order.getId());
                    assertEquals(NEW, order.getStatus());
                    assertEquals(1, order.getLineItems().size()); })
                .assertNext(order -> assertEquals(SUBMITTED, order.getStatus()))
                .assertNext(order -> assertEquals(PROCESSED, order.getStatus()))
                .assertNext(order -> assertEquals(SHIPPED, order.getStatus()))
                .thenCancel()
                .verify();
    }

    @Test
    public void streamsOrderEvents() {

        LocalDateTime now = now();
        LineItem lineItem = mock(LineItem.class);

        OrderEventStore eventStore = mock(OrderEventStore.class);
        when(eventStore.findAllByEntityIdOrderByEventCreateTimeAsc(orderId)).thenReturn(Flux.just(
                new OrderCreated(randomUUID(), orderId, now, Set.of(lineItem)),
                new OrderSubmitted(randomUUID(), orderId, now),
                new OrderProcessed(randomUUID(), orderId, now),
                new OrderShipped(randomUUID(), orderId, now)
        ));

        StepVerifier.create(new OrderEventsProcessor(eventStore, defaultSink).streamEvents(orderId))
                .assertNext(event -> assertTrue(event instanceof OrderCreated))
                .assertNext(event -> assertTrue(event instanceof OrderSubmitted))
                .assertNext(event -> assertTrue(event instanceof OrderProcessed))
                .assertNext(event -> assertTrue(event instanceof OrderShipped))
                .thenCancel()
                .verify();
    }

    @Test
    public void streamsOrderEventsBySpecifiedEventType() {

        OrderEventStore eventStore = mock(OrderEventStore.class);

        Sinks.Many<OrderEvent> sink = mock(Sinks.Many.class);
        when(sink.asFlux()).thenReturn(Flux.just(
                new OrderSubmitted(randomUUID(), Id.random(), now()),
                new OrderShipped(randomUUID(), Id.random(), now()),
                new OrderSubmitted(randomUUID(), Id.random(), now()),
                new OrderShipped(randomUUID(), Id.random(), now())
        ));

        StepVerifier.create(new OrderEventsProcessor(eventStore, sink).streamEvents(OrderSubmitted.class))
                .assertNext(event -> assertTrue(event instanceof OrderSubmitted))
                .assertNext(event -> assertTrue(event instanceof OrderSubmitted))
                .thenCancel()
                .verify();

        StepVerifier.create(new OrderEventsProcessor(eventStore, sink).streamEvents(OrderShipped.class))
                .assertNext(event -> assertTrue(event instanceof OrderShipped))
                .assertNext(event -> assertTrue(event instanceof OrderShipped))
                .thenCancel()
                .verify();
    }

    @Test
    public void canPublishGivenEvent() {
        OrderEvent event = new OrderShipped(randomUUID(), Id.random(), now());

        OrderEventStore eventStore = mock(OrderEventStore.class);
        Sinks.Many<OrderEvent> sink = mock(Sinks.Many.class);
        when(eventStore.save(event)).thenReturn(Mono.just(event));

        StepVerifier.create(new OrderEventsProcessor(eventStore, sink).publish(event))
                .verifyComplete();
        verify(sink, times(1)).tryEmitNext(event);

    }

    @Test
    public void reconstructsStateOnGivenTime() {
        LocalDateTime requestedTime = parse("2021-08-30T13:00:00");

        OrderEventStore eventStore = mock(OrderEventStore.class);
        when(eventStore.findAllByEntityIdAndEventCreateTimeLessThanEqualOrderByEventCreateTimeAsc(orderId, requestedTime))
                .thenReturn(Flux.just(
                        new OrderCreated(randomUUID(), orderId, requestedTime.minusMinutes(20), Set.of(mock(LineItem.class), mock(LineItem.class))),
                        new OrderSubmitted(randomUUID(), orderId, requestedTime.minusMinutes(15)),
                        new OrderProcessed(randomUUID(), orderId, requestedTime.minusMinutes(10)),
                        new OrderShipped(randomUUID(), orderId, requestedTime.minusMinutes(5))
                ));

        StepVerifier.create(new OrderEventsProcessor(eventStore, defaultSink).reconstructState(orderId, requestedTime))
                .assertNext(order -> {
                    assertEquals(SHIPPED, order.getStatus());
                    assertEquals(requestedTime.minusMinutes(5), order.getUpdatedAt());
                    assertEquals(2, order.getLineItems().size());
                }).verifyComplete();
    }

}
