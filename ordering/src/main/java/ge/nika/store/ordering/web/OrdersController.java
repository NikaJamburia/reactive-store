package ge.nika.store.ordering.web;

import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.service.OrderService;
import ge.nika.store.ordering.service.request.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public Mono<Order> addUser(@RequestBody CreateOrderRequest createOrderRequest) {
        return orderService.createOrder(createOrderRequest);
    }

    @GetMapping(value = "/orders/{id}/state-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Order> getEvents(@PathVariable("id") String id) {
        return orderService.streamOrderState(id);
    }
}
