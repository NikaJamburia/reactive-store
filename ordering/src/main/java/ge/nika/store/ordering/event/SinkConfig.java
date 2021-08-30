package ge.nika.store.ordering.event;

import ge.nika.store.ordering.domain.order.model.OrderEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class SinkConfig {

    @Bean
    public Sinks.Many<OrderEvent> orderEventsSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}
