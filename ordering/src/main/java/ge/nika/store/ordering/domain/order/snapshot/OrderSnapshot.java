package ge.nika.store.ordering.domain.order.snapshot;

import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.order.model.OrderStatus;
import ge.nika.store.ordering.domain.order.value.LineItem;
import ge.nika.store.ordering.snapshot.Snapshot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order_snapshots")
public class OrderSnapshot implements Snapshot<Order> {

    @Id
    private UUID id;
    private LocalDateTime createdAt;
    private ge.nika.store.ordering.domain.value.Id orderId;
    private OrderStatus orderStatus;
    private LocalDateTime orderUpdatedAt;
    private Set<LineItem> lineItems;

    @Override
    public Order getSavedState() {
        return new Order(orderId, orderStatus, orderUpdatedAt, lineItems);
    }
}
