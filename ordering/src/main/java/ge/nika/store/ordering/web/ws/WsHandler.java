package ge.nika.store.ordering.web.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class WsHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        return session.send(session.receive()); 

    }
}
