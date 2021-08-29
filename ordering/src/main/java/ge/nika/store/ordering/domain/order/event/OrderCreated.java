package ge.nika.store.ordering.domain.order.event;

import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.order.model.OrderEvent;
import ge.nika.store.ordering.domain.order.model.OrderStatus;
import ge.nika.store.ordering.domain.order.value.LineItem;
import ge.nika.store.ordering.domain.value.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@TypeAlias("OrderCreated")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderCreated extends OrderEvent {

    private Set<LineItem> lineItems;

    public OrderCreated(UUID id, Id entityId, LocalDateTime eventCreateTime, Set<LineItem> lineItems) {
        super(id, entityId, eventCreateTime);
        this.lineItems = lineItems;
    }

    @Override
    protected Order applyToOrder(Order entity) {
        return new Order(entityId, OrderStatus.NEW, eventCreateTime, lineItems);
    }

}
