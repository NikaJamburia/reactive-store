package ge.nika.store.ordering.event;

import ge.nika.store.ordering.domain.order.event.LineItemAdded;
import ge.nika.store.ordering.domain.order.event.OrderCreated;
import ge.nika.store.ordering.domain.order.event.OrderProcessed;
import ge.nika.store.ordering.domain.order.event.OrderSubmitted;
import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.order.model.OrderEvent;
import ge.nika.store.ordering.domain.order.value.LineItem;
import ge.nika.store.ordering.domain.value.Id;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Set;

import static ge.nika.store.ordering.domain.order.model.OrderStatus.*;
import static java.util.UUID.*;
import static org.mockito.Mockito.mock;

public class StateStreamTest {

    @Test
    public void streamsStateChangesOfGivenEntityAccordingToGivenEventSource() {
        Id orderId = Id.random();
        LocalDate today = LocalDate.now();

        LineItem lineItem1 = mock(LineItem.class);
        LineItem lineItem2 = mock(LineItem.class);
        LineItem lineItem3 = mock(LineItem.class);

        Flux<OrderEvent> events = Flux.just(
                new OrderCreated(randomUUID(), orderId, today.atTime(12, 5), Set.of(lineItem1, lineItem2)),
                new LineItemAdded(randomUUID(), orderId, today.atTime(12, 10), lineItem3),
                new OrderSubmitted(randomUUID(), orderId, today.atTime(12, 15)),
                new OrderProcessed(randomUUID(), orderId, today.atTime(12, 20))
        );

        StateStream<Order, OrderEvent> stream = new StateStream<>(Order.empty(orderId), events);
        StepVerifier.create(stream.get())
                .expectNext(new Order(orderId, NEW, today.atTime(12, 5), Set.of(lineItem1, lineItem2)))
                .expectNext(new Order(orderId, NEW, today.atTime(12, 10), Set.of(lineItem1, lineItem2, lineItem3)))
                .expectNext(new Order(orderId, SUBMITTED, today.atTime(12, 15), Set.of(lineItem1, lineItem2, lineItem3)))
                .expectNext(new Order(orderId, PROCESSED, today.atTime(12, 20), Set.of(lineItem1, lineItem2, lineItem3)))
                .thenCancel()
                .verify();
    }
}
