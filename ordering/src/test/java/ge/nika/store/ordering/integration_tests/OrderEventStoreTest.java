package ge.nika.store.ordering.integration_tests;

import ge.nika.store.ordering.domain.order.event.OrderCreated;
import ge.nika.store.ordering.domain.order.event.OrderProcessed;
import ge.nika.store.ordering.domain.order.event.OrderShipped;
import ge.nika.store.ordering.domain.order.event.OrderSubmitted;
import ge.nika.store.ordering.domain.order.value.LineItem;
import ge.nika.store.ordering.domain.order.value.Product;
import ge.nika.store.ordering.domain.value.Id;
import ge.nika.store.ordering.repository.OrderEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.time.LocalDateTime.*;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class OrderEventStoreTest {

    @Autowired
    private OrderEventStore orderEventStore;

    @BeforeEach
    public void cleanUp() {
        orderEventStore.deleteAll().block();
    }

    @Test
    public void correctlySavesEvent() {
        OrderShipped event = new OrderShipped(UUID.randomUUID(), Id.random(), now().withNano(0));
        orderEventStore.save(event).block();
        StepVerifier.create(orderEventStore.findById(event.getId()))
                .assertNext(eventFromStore -> {
                    assertEquals(event.getId(), eventFromStore.getId());
                    assertEquals(event.getEventCreateTime(), eventFromStore.getEventCreateTime());
                    assertEquals(event.getEntityId().getValue(), eventFromStore.getEntityId().getValue());
                })
                .verifyComplete();
    }

    @Test
    public void findsEventsForOrderChronologically() {
        Id orderId = Id.random();
        orderEventStore.saveAll(List.of(
                new OrderShipped(randomUUID(), orderId, parse("2021-08-30T12:00:00.004")),
                new OrderProcessed(randomUUID(), orderId, parse("2021-08-30T12:00:00.003")),
                new OrderCreated(randomUUID(), orderId, parse("2021-08-30T12:00:00.001"), Set.of(new LineItem(Id.random(), new Product(Id.random()), 2))),
                new OrderSubmitted(randomUUID(), orderId, parse("2021-08-30T12:00:00.002"))
        )).blockLast();

        StepVerifier.create(orderEventStore.getAllEvents(orderId))
                .assertNext(event -> assertTrue(event instanceof OrderCreated))
                .assertNext(event -> assertTrue(event instanceof OrderSubmitted))
                .assertNext(event -> assertTrue(event instanceof OrderProcessed))
                .assertNext(event -> assertTrue(event instanceof OrderShipped))
                .verifyComplete();
    }

    @Test
    public void findsEventsForOrderUpToSpecifiedTime() {
        Id orderId = Id.random();
        orderEventStore.saveAll(List.of(
                new OrderShipped(randomUUID(), orderId, parse("2021-08-30T12:00:00.004")),
                new OrderProcessed(randomUUID(), orderId, parse("2021-08-30T12:00:00.003")),
                new OrderCreated(randomUUID(), orderId, parse("2021-08-30T12:00:00.001"), Set.of(new LineItem(Id.random(), new Product(Id.random()), 2))),
                new OrderSubmitted(randomUUID(), orderId, parse("2021-08-30T12:00:00.002"))
        )).blockLast();

        StepVerifier.create(orderEventStore.getEventsUpToTime(orderId, parse("2021-08-30T12:00:00.001")))
                .assertNext(event -> assertTrue(event instanceof OrderCreated))
                .verifyComplete();

        StepVerifier.create(orderEventStore.getEventsUpToTime(orderId, parse("2021-08-30T12:00:00.002")))
                .assertNext(event -> assertTrue(event instanceof OrderCreated))
                .assertNext(event -> assertTrue(event instanceof OrderSubmitted))
                .verifyComplete();

        StepVerifier.create(orderEventStore.getEventsUpToTime(orderId, parse("2021-08-30T12:00:00.003")))
                .assertNext(event -> assertTrue(event instanceof OrderCreated))
                .assertNext(event -> assertTrue(event instanceof OrderSubmitted))
                .assertNext(event -> assertTrue(event instanceof OrderProcessed))
                .verifyComplete();

        StepVerifier.create(orderEventStore.getEventsUpToTime(orderId, parse("2021-08-30T12:00:00.004")))
                .assertNext(event -> assertTrue(event instanceof OrderCreated))
                .assertNext(event -> assertTrue(event instanceof OrderSubmitted))
                .assertNext(event -> assertTrue(event instanceof OrderProcessed))
                .assertNext(event -> assertTrue(event instanceof OrderShipped))
                .verifyComplete();
    }
}
