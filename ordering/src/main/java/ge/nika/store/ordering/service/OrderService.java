package ge.nika.store.ordering.service;

import ge.nika.store.ordering.domain.order.event.OrderCreated;
import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.order.model.OrderStatus;
import ge.nika.store.ordering.domain.order.value.LineItem;
import ge.nika.store.ordering.domain.value.Id;
import ge.nika.store.ordering.service.request.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class OrderService {

    private final OrderEventsProcessor orderEvents;

    public Mono<Order> createOrder(CreateOrderRequest request) {
        return Mono.create(sink -> {
            Order order = new Order(Id.random(), OrderStatus.NEW, request.getCreateTime(),
                    request.getLineItemData()
                            .stream()
                            .map(data -> new LineItem(Id.random(), data.getProduct(), data.getAmount()))
                            .collect(Collectors.toUnmodifiableSet()));

            orderEvents.publish(new OrderCreated(UUID.randomUUID(), order.getId(), order.getUpdatedAt(), order.getLineItems()))
                    .subscribe(value -> {}, err -> {}, () -> sink.success(order));
        });
    }

    public Flux<Order> streamOrderState(String orderId) {
        return orderEvents.streamState(new Id(orderId));
    }
}
