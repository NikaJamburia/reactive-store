package ge.nika.store.ordering.domain.order.event;

import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.order.value.LineItem;
import ge.nika.store.ordering.domain.order.value.Product;
import ge.nika.store.ordering.domain.value.Id;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class LineItemUpdatedTest {

    @Test
    public void removesGivenLineItemAndAddsUpdatedVersion() {
        Id orderId = Id.random();

        LineItem oldItem = new LineItem(Id.random(), new Product(Id.random()), 1);
        LineItem updatedItem = new LineItem(oldItem.getId(), oldItem.getProduct(), 2);
        LocalDateTime updateDate = LocalDateTime.now();

        Order order = mock(Order.class);
        Order removed = mock(Order.class);
        when(order.removeLineItem(oldItem.getId(), updateDate)).thenReturn(removed);

        LineItemUpdated event = new LineItemUpdated(UUID.randomUUID(), orderId, updateDate, oldItem.getId(), updatedItem);
        event.applyTo(order);

        verify(order, times(1)).removeLineItem(oldItem.getId(), updateDate);
        verify(removed, times(1)).addLineItem(updatedItem, updateDate);
    }
}
