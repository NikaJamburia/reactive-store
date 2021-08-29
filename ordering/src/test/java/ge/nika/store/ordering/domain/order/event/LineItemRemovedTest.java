package ge.nika.store.ordering.domain.order.event;

import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.order.value.LineItem;
import ge.nika.store.ordering.domain.order.value.Product;
import ge.nika.store.ordering.domain.value.Id;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class LineItemRemovedTest {

    @Test
    public void removesGivenLineItemFromOrderWhenApplied() {
        Id orderId = Id.random();
        LineItem newLineItem = new LineItem(Id.random(), new Product(Id.random()), 1);
        LocalDateTime removeDate = LocalDateTime.now();
        Order order = mock(Order.class);

        LineItemRemoved event = new LineItemRemoved(UUID.randomUUID(), orderId, removeDate, newLineItem.getId());
        event.applyTo(order);

        verify(order, times(1)).removeLineItem(newLineItem.getId(), removeDate);
    }
}
