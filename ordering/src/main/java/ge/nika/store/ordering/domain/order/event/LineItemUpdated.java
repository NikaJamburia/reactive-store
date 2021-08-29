package ge.nika.store.ordering.domain.order.event;

import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.order.model.OrderEvent;
import ge.nika.store.ordering.domain.order.value.LineItem;
import ge.nika.store.ordering.domain.value.Id;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.TypeAlias;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@TypeAlias("LineItemUpdated")
public class LineItemUpdated extends OrderEvent {

    private final Id lineItemId;
    private final LineItem updatedLineItem;

    public LineItemUpdated(UUID id, Id entityId, LocalDateTime eventCreateTime, Id lineItemId, LineItem updatedLineItem) {
        super(id, entityId, eventCreateTime);
        this.lineItemId = lineItemId;
        this.updatedLineItem = updatedLineItem;
    }

    @Override
    protected Order applyToOrder(Order entity) {
        return entity
                .removeLineItem(lineItemId, eventCreateTime)
                .addLineItem(updatedLineItem, eventCreateTime);
    }

}

