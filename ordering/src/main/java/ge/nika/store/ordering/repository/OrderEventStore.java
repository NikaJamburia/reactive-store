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
    Flux<OrderEvent> findAllByEntityIdAndEventCreateTimeGreaterThanEqualAndEventCreateTimeLessThanEqualOrderByEventCreateTimeAsc(Id entityId, LocalDateTime from, LocalDateTime to);

    default Flux<OrderEvent> getAllEvents(Id orderId) {
        return findAllByEntityIdOrderByEventCreateTimeAsc(orderId);
    }

    default Flux<OrderEvent> getEventsUpToTime(Id orderId, LocalDateTime time) {
        return findAllByEntityIdAndEventCreateTimeLessThanEqualOrderByEventCreateTimeAsc(orderId, time);
    }

    default Flux<OrderEvent> getEventsInTimeFrame(Id orderId, LocalDateTime from, LocalDateTime to) {
        return findAllByEntityIdAndEventCreateTimeGreaterThanEqualAndEventCreateTimeLessThanEqualOrderByEventCreateTimeAsc(orderId, from, to);
    }
}
