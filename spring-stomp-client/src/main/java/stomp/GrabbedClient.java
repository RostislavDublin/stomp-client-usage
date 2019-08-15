package stomp;

import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lu
 * @Description: Main class - test monitor incoming transactions
 * @date 2017.03.07
 */
public class GrabbedClient {

    public static void main(String[] args) {
        // create WebSocket client
        List<Transport> transports = new ArrayList<Transport>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketClient transport = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(transport);
        stompClient.setMessageConverter(new StringMessageConverter());
        StompSessionHandler handler = new StompSessionHandlerImpl();
        stompClient.connect("ws://10.211.55.3:61613", handler);
    }

}