package ge.nika.store.ordering.service.request;

import ge.nika.store.ordering.domain.order.value.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    private List<LineItemData> lineItemData;
    private LocalDateTime createTime;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class LineItemData {
        private Product product;
        private Integer amount;
    }
}
