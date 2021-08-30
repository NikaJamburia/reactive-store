package ge.nika.store.ordering.service;

import ge.nika.store.ordering.domain.order.model.Order;
import ge.nika.store.ordering.domain.order.snapshot.OrderSnapshot;
import ge.nika.store.ordering.domain.value.Id;
import ge.nika.store.ordering.repository.OrderSnapshotRepository;
import ge.nika.store.ordering.snapshot.SnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderSnapshotService implements SnapshotService<Order, OrderSnapshot> {

    private final OrderSnapshotRepository snapshotRepository;

    @Override
    public Mono<OrderSnapshot> getLatestSnapshot(Id entityId) {
        return snapshotRepository.findTopByOrderIdOrderByCreatedAtDesc(entityId);
    }

    @Override
    public Mono<OrderSnapshot> createSnapshot(Order entity, LocalDateTime onTime) {
        return snapshotRepository.save(new OrderSnapshot(
                UUID.randomUUID(),
                onTime,
                entity.getId(),
                entity.getStatus(),
                entity.getUpdatedAt(),
                entity.getLineItems()
        ));
    }

}
