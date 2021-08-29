package ge.nika.store.ordering.domain.value;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Id {
    private String value;

    public static Id random() {
        return new Id(UUID.randomUUID().toString());
    }
}
