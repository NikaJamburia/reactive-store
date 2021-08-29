package ge.nika.store.ordering.domain.order.model;

import ge.nika.store.ordering.domain.order.value.LineItem;
import ge.nika.store.ordering.domain.value.Id;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ge.nika.store.ordering.domain.order.model.OrderStatus.*;

@Value
public class Order {
    Id id;
    OrderStatus status;
    LocalDateTime updatedAt;
    Set<LineItem> lineItems;

    public Order addLineItem(LineItem lineItem, LocalDateTime onTime) {
        requireEitherStatus("Orders Line items can no longer be modified", NEW);
        return new Order(id, status, onTime,
                Stream.concat(
                        lineItems.stream(),
                        Stream.of(lineItem)
                ).collect(Collectors.toUnmodifiableSet()));
    }

    public Order removeLineItem(Id lineItemId, LocalDateTime onTime) {
        requireEitherStatus("Orders Line items can no longer be modified", NEW);
        return new Order(id, status, onTime,
                lineItems
                        .stream()
                        .filter(li -> !li.getId().equals(lineItemId)).collect(Collectors.toSet())
        );
    }

    public Order submit(LocalDateTime onTime) {
        requireEitherStatus("Only new orders can be submitted", NEW);
        return withNewStatus(SUBMITTED, onTime);
    }

    public Order process(LocalDateTime onTime) {
        requireEitherStatus("Only submitted orders can be processed", SUBMITTED);
        return withNewStatus(PROCESSED, onTime);
    }

    public Order ship(LocalDateTime onTime) {
        requireEitherStatus("Only processed orders can be shipped", PROCESSED);
        return withNewStatus(SHIPPED, onTime);
    }

    public Order cancel(LocalDateTime onTime) {
        requireEitherStatus("Only new and submitted orders can be cancelled", NEW, SUBMITTED);
        return withNewStatus(CANCELLED, onTime);
    }

    private Order withNewStatus(OrderStatus status, LocalDateTime onTime) {
        return new Order(id, status, onTime, lineItems);
    }

    private void requireEitherStatus(String errorMsg, OrderStatus... statuses) {
        if (Arrays.stream(statuses).noneMatch(status -> this.status == status)) {
            throw new IllegalStateException(errorMsg);
        }
    }

    public static Order empty(Id id) {
        return new Order(id, NEW, LocalDateTime.now(), Set.of());
    }
    public static Order empty() {
        return new Order(Id.random(), NEW, LocalDateTime.now(), Set.of());
    }
}
