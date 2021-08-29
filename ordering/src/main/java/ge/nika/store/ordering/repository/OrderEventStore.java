package ge.nika.store.ordering.repository;

import ge.nika.store.ordering.domain.order.model.OrderEvent;
import ge.nika.store.ordering.domain.value.Id;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface OrderEventStore extends ReactiveMongoRepository<OrderEvent, String> {

    Flux<OrderEvent> findAllOrderByEventCreateTimeAsc();
    Flux<OrderEvent> findAllByEntityIdAndEventCreateTimeBeforeOrderByEventCreateTimeAsc(Id entityId, LocalDateTime time);
    Flux<OrderEvent> findAllByEntityIdOrderByEventCreateTimeAsc(Id entityId);

    @Query(value="{ '_class' : ?0 }")
    Flux<OrderEvent> findEventsByClassNameOrderByEventCreateTimeAsc(String className);
}
