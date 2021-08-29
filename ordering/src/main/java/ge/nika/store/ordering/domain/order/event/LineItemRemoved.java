package ge.nika.store.ordering.domain.order.event;

import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.order.model.OrderEvent;
import ge.nika.store.ordering.domain.value.Id;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.TypeAlias;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@TypeAlias("LineItemRemoved")
public class LineItemRemoved extends OrderEvent {

    private final Id lineItemId;

    public LineItemRemoved(UUID id, Id entityId, LocalDateTime eventCreateTime, Id lineItemId) {
        super(id, entityId, eventCreateTime);
        this.lineItemId = lineItemId;
    }

    @Override
    protected Order applyToOrder(Order entity) {
        return entity.removeLineItem(lineItemId, eventCreateTime);
    }

}
