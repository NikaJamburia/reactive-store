package ge.nika.store.ordering.repository;

import ge.nika.store.ordering.domain.order.model.OrderEvent;
import ge.nika.store.ordering.domain.value.Id;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OrderEventStore extends ReactiveMongoRepository<OrderEvent, UUID> {

    Flux<OrderEvent> findAllByEntityIdOrderByEventCreateTimeAsc(Id entityId);
    Flux<OrderEvent> findAllByEntityIdAndEventCreateTimeLessThanEqualOrderByEventCreateTimeAsc(Id entityId, LocalDateTime time);
}
