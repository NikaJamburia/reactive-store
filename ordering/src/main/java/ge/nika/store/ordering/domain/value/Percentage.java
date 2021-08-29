package ge.nika.store.ordering.domain.value;

import lombok.Value;

@Value
public class Percentage {
    int value;

    public Percentage(int value) {
        if(value < 0) {
            throw new IllegalStateException("Percentage cant be less then 0");
        }
        this.value = value;
    }
}
