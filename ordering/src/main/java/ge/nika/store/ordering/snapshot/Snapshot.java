package ge.nika.store.ordering.snapshot;

import java.time.LocalDateTime;

public interface Snapshot<T> {
    LocalDateTime getCreatedAt();
    T getSavedState();
}
