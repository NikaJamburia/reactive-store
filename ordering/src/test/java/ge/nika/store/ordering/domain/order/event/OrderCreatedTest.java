package ge.nika.store.ordering.domain.order.event;

import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.order.model.OrderStatus;
import ge.nika.store.ordering.domain.order.value.LineItem;
import ge.nika.store.ordering.domain.value.Id;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class OrderCreatedTest {

    @Test
    public void createsNewOrder() {
        Id orderId = Id.random();
        LocalDateTime createDate = LocalDateTime.now();

        Order order = new OrderCreated(UUID.randomUUID(), orderId, createDate, Set.of(mock(LineItem.class), mock(LineItem.class)))
                .applyTo(Order.empty());

        assertEquals(orderId, order.getId());
        assertEquals(OrderStatus.NEW, order.getStatus());
        assertEquals(createDate, order.getUpdatedAt());
        assertEquals(2, order.getLineItems().size());
    }
}
