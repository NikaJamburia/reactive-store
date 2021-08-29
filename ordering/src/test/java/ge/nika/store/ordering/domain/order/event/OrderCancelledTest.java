package ge.nika.store.ordering.domain.order.event;

import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.value.Id;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class OrderCancelledTest {

    @Test
    public void cancelsOrder() {
        Id orderId = Id.random();
        Order order = mock(Order.class);
        LocalDateTime cancelDate = LocalDateTime.now();

        new OrderCancelled(UUID.randomUUID(), orderId, cancelDate)
                .applyTo(order);

        verify(order, times(1)).cancel(cancelDate);
    }
}
