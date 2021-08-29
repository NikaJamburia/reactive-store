package ge.nika.store.ordering.domain.order.value;

import ge.nika.store.ordering.domain.value.Id;
import lombok.Value;

@Value
public class LineItem {
    Id id;
    Product product;
    Integer itemsCount;
}
