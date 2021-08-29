package ge.nika.store.ordering.domain.value;

import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.valueOf;

@Value
public class MonetaryAmount {
    BigDecimal value;
    Currency currency;

    public MonetaryAmount(double value, Currency currency) {
        this.value = valueOf(value).setScale(2, RoundingMode.DOWN);
        this.currency = currency;
    }

    public MonetaryAmount(BigDecimal value, Currency currency) {
        this.value = value.setScale(2, RoundingMode.DOWN);
        this.currency = currency;
    }


    @Override
    public String toString() {
        return value.toString() + " " + currency;
    }

    public MonetaryAmount add(MonetaryAmount other) {
        if (this.currency != other.currency) {
            throw new IllegalStateException("Cant add money of different currencies");
        }
        return new MonetaryAmount(this.getValue().add(other.getValue()), this.getCurrency());
    }

    public MonetaryAmount multiply(Double other) {
        return new MonetaryAmount(this.getValue().multiply(valueOf(other)), this.getCurrency());
    }
}

