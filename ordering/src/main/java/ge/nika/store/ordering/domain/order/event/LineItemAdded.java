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
@TypeAlias("LineItemAdded")
public class LineItemAdded extends OrderEvent {

    private final LineItem lineItem;

    public LineItemAdded(UUID id, Id entityId, LocalDateTime eventCreateTime, LineItem lineItem) {
        super(id, entityId, eventCreateTime);
        this.lineItem = lineItem;
    }

    @Override
    protected Order applyToOrder(Order entity) {
        return entity.addLineItem(lineItem, eventCreateTime);
    }

}
