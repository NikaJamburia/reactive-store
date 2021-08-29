package ge.nika.store.ordering.domain.order.model;

import ge.nika.store.ordering.domain.order.value.LineItem;
import ge.nika.store.ordering.domain.order.value.Product;
import ge.nika.store.ordering.domain.value.Id;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderTest {

    private final Product bag = new Product(new Id("bag"));
    private final Product shoes = new Product(new Id("shoes"));
    private final LineItem twoBags = new LineItem(Id.random(), bag, 2);
    private final LineItem fourShoes = new LineItem(Id.random(), shoes, 4);

    @Test
    public void lineItemsCanBeAdded() {
        Order order = Order.empty();

        LocalDateTime twoBagsAddDate = now();
        Order orderWithBag = order.addLineItem(twoBags, twoBagsAddDate);

        assertEquals(1, orderWithBag.getLineItems().size());
        assertEquals(twoBags, orderWithBag.getLineItems().stream().findFirst().get());
        assertEquals(twoBagsAddDate, orderWithBag.getUpdatedAt());
        assertEquals(1, orderWithBag.addLineItem(twoBags, twoBagsAddDate).getLineItems().size());

    }

    @Test
    public void lineItemsCanBeRemoved() {
        Order orderWithBagsAndShoes = Order.empty().addLineItem(fourShoes, now()).addLineItem(twoBags, now());
        assertEquals(2, orderWithBagsAndShoes.getLineItems().size());
        assertEquals(1, orderWithBagsAndShoes.removeLineItem(fourShoes.getId(), now()).getLineItems().size());
        assertEquals(0, orderWithBagsAndShoes
                .removeLineItem(fourShoes.getId(), now())
                .removeLineItem(twoBags.getId(), now())
                .getLineItems().size());
    }

    @Test
    public void submittedOrdersLineItemsCanNoLongerBeModified() {
        Order submittedOrder = Order.empty()
                .addLineItem(fourShoes, now())
                .submit(now());

        assertThrows(IllegalStateException.class,
                () -> submittedOrder.removeLineItem(fourShoes.getId(), now()),
                "Orders Line items can no longer be modified");

        assertThrows(IllegalStateException.class,
                () -> submittedOrder.addLineItem(twoBags, now()),
                "Orders Line items can no longer be modified");
    }

    @Test
    public void newOrderCanBeSubmitted() {
        Order order = Order.empty();

        assertEquals(OrderStatus.SUBMITTED, order.submit(now()).getStatus());
        assertThrows(IllegalStateException.class, () -> order.submit(now()).submit(now()), "Only new orders can be submitted");
        assertThrows(IllegalStateException.class, () -> order.submit(now()).process(now()).submit(now()), "Only new orders can be submitted");
        assertThrows(IllegalStateException.class, () -> order.submit(now()).process(now()).ship(now()).submit(now()), "Only new orders can be submitted");
    }

    @Test
    public void submittedOrderCanBeProcessed() {
        Order order = Order.empty().submit(now());

        assertEquals(OrderStatus.PROCESSED, order.process(now()).getStatus());
        assertThrows(IllegalStateException.class, () -> Order.empty().process(now()), "Only submitted orders can be processed");
        assertThrows(IllegalStateException.class, () -> order.process(now()).process(now()), "Only submitted orders can be processed");
        assertThrows(IllegalStateException.class, () -> order.process(now()).ship(now()).process(now()), "Only submitted orders can be processed");

    }

    @Test
    public void processedOrdersCanBeShipped() {
        Order order = Order.empty().submit(now()).process(now());

        assertEquals(OrderStatus.SHIPPED, order.ship(now()).getStatus());
        assertThrows(IllegalStateException.class, () -> Order.empty().ship(now()), "Only processed orders can be shipped");
        assertThrows(IllegalStateException.class, () -> Order.empty().submit(now()).ship(now()), "Only processed orders can be shipped");
        assertThrows(IllegalStateException.class, () -> Order.empty().submit(now()).ship(now()), "Only processed orders can be shipped");
        assertThrows(IllegalStateException.class, () -> order.ship(now()).ship(now()), "Only processed orders can be shipped");

    }

    @Test
    public void newAndSubmittedOrdersCanBeCancelled() {
        Order order = Order.empty();

        assertEquals(OrderStatus.CANCELLED, order.cancel(now()).getStatus());
        assertEquals(OrderStatus.CANCELLED, order.submit(now()).cancel(now()).getStatus());
        assertThrows(IllegalStateException.class, () -> order.submit(now()).process(now()).cancel(now()), "Only new and submitted orders can be cancelled");
        assertThrows(IllegalStateException.class, () -> order.submit(now()).process(now()).ship(now()).cancel(now()), "Only new and submitted orders can be cancelled");

    }

    @Test
    public void cancelledOrderCantBeChanged() {
        Order order = Order
                .empty()
                .addLineItem(twoBags, now())
                .cancel(now());

        assertThrows(IllegalStateException.class, () -> order.submit(now()));
        assertThrows(IllegalStateException.class, () -> order.process(now()));
        assertThrows(IllegalStateException.class, () -> order.ship(now()));
        assertThrows(IllegalStateException.class, () -> order.cancel(now()));
        assertThrows(IllegalStateException.class, () -> order.addLineItem(fourShoes, now()));
        assertThrows(IllegalStateException.class, () -> order.removeLineItem(twoBags.getId(), now()));

    }

}
