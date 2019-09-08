package spring;

import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.io.IOException;

public class TcpStompClientRun {
    public static void main(String[] args) throws IOException {

        TcpStompClient stompClient = new TcpStompClient();
        stompClient.setMessageConverter(new StringMessageConverter());

        StompSessionHandler sessionHandler = new MyStompSessionHandler();
        stompClient.connect(sessionHandler);
    }

    public static class MyStompSessionHandler extends StompSessionHandlerAdapter {

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("After connection!");
        }
    }}
