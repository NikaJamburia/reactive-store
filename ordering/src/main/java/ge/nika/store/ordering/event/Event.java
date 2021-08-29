package ge.nika.store.ordering.event;

import ge.nika.store.ordering.domain.value.Id;

import java.time.LocalDateTime;

public interface Event<T> {
    T applyTo(T entity);
    Id getEntityId();
    LocalDateTime getEventCreateTime();
}
