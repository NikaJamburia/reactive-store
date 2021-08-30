package ge.nika.store.ordering.repository;

import ge.nika.store.ordering.domain.order.snapshot.OrderSnapshot;
import ge.nika.store.ordering.domain.value.Id;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderSnapshotRepository extends ReactiveMongoRepository<OrderSnapshot, UUID> {

    Mono<OrderSnapshot> findTopByOrderIdOrderByCreatedAtDesc(Id orderId);
}
