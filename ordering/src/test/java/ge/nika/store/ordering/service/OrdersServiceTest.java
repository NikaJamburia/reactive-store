package ge.nika.store.ordering.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ge.nika.store.ordering.domain.order.value.Product;
import ge.nika.store.ordering.domain.value.Id;
import ge.nika.store.ordering.service.request.CreateOrderRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

public class OrdersServiceTest {

    @Test
    public void aaa() throws JsonProcessingException {
        List<CreateOrderRequest.LineItemData> lineItemData = List.of(
                new CreateOrderRequest.LineItemData(new Product(new Id("ragaca")), 1),
                new CreateOrderRequest.LineItemData(new Product(new Id("rugaca")), 2)
        );

        CreateOrderRequest request = new CreateOrderRequest(lineItemData, LocalDateTime.now());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.writeValueAsString(request);

    }
}
