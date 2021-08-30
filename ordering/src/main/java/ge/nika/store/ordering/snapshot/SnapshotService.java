package ge.nika.store.ordering.snapshot;

import ge.nika.store.ordering.domain.value.Id;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface SnapshotService<E, S extends Snapshot<E>> {
    Mono<S> getLatestSnapshot(Id entityId);
    Mono<S> createSnapshot(E entity, LocalDateTime onTime);
}
