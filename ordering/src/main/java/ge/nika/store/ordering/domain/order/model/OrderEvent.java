package ge.nika.store.ordering.domain.order.model;

import ge.nika.store.ordering.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "order_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class OrderEvent implements Event<Order> {

    @Id
    protected UUID id;
    protected ge.nika.store.ordering.domain.value.Id entityId;
    protected LocalDateTime eventCreateTime;

    @Override
    public Order applyTo(Order entity) {
        try {
            return applyToOrder(entity);
        } catch (IllegalStateException e) {
            return entity;
        }
    }

    abstract protected Order applyToOrder(Order entity);
}
