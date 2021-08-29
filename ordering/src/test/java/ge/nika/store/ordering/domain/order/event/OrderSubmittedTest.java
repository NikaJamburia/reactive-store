package ge.nika.store.ordering.domain.order.event;

import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.value.Id;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class OrderSubmittedTest {

    @Test
    public void submitsOrder() {
        Id orderId = Id.random();
        Order order = mock(Order.class);
        LocalDateTime time = LocalDateTime.now();

        new OrderSubmitted(UUID.randomUUID(), orderId, time)
                .applyTo(order);

        verify(order, times(1)).submit(time);
    }
}
