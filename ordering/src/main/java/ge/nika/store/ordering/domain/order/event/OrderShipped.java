package ge.nika.store.ordering.domain.order.event;

import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.order.model.OrderEvent;
import ge.nika.store.ordering.domain.value.Id;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.TypeAlias;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@TypeAlias("OrderShipped")
public class OrderShipped extends OrderEvent {

    public OrderShipped(UUID id, Id entityId, LocalDateTime eventCreateTime) {
        super(id, entityId, eventCreateTime);
    }

    @Override
    protected Order applyToOrder(Order entity) {
        return entity.ship(this.eventCreateTime);
    }

}
