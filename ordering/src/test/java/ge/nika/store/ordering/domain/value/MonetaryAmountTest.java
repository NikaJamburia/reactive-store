package ge.nika.store.ordering.domain.value;

import org.junit.jupiter.api.Test;

import static ge.nika.store.ordering.domain.value.Currency.GEL;
import static ge.nika.store.ordering.domain.value.Currency.USD;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MonetaryAmountTest {

    @Test
    public void correctlyScalesGivenValue() {
        assertEquals(valueOf(10.14), new MonetaryAmount(10.1499, GEL).getValue());
        assertEquals(valueOf(5.01), new MonetaryAmount(5.011, GEL).getValue());
        assertEquals(valueOf(3.14), new MonetaryAmount(Math.PI, GEL).getValue());
        assertEquals(valueOf(10.00).setScale(2), new MonetaryAmount(10, GEL).getValue());
    }

    @Test
    public void correctlyCreatesAtring() {
        assertEquals("10.14 GEL", new MonetaryAmount(10.1499, GEL).toString());
        assertEquals("5.01 USD", new MonetaryAmount(5.011, USD).toString());
        assertEquals("3.14 USD", new MonetaryAmount(Math.PI, USD).toString());
        assertEquals("10.00 USD", new MonetaryAmount(10, USD).toString());
    }

    @Test
    public void addsOtherMonetaryAmountToItself() {
        MonetaryAmount piPlusPi = new MonetaryAmount(Math.PI, GEL).add(new MonetaryAmount(Math.PI, GEL));
        assertEquals(valueOf(6.28), piPlusPi.getValue());
        assertEquals(valueOf(4.14), new MonetaryAmount(1, GEL).add(new MonetaryAmount(Math.PI, GEL)).getValue());
    }

    @Test
    public void canBeMultiplied() {
        assertEquals(valueOf(6.28), new MonetaryAmount(Math.PI, GEL).multiply(2.0).getValue());
        assertEquals(valueOf(5.00).setScale(2), new MonetaryAmount(2, GEL).multiply(2.5).getValue());
        assertEquals(valueOf(35.87), new MonetaryAmount(10.25, GEL).multiply(3.5).getValue());
    }
}
