import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.stomp.StompClient;
import io.vertx.ext.stomp.StompClientConnection;
import io.vertx.ext.stomp.StompClientOptions;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VertxStompClientTest {
    private static VertxStompClientTest client = new VertxStompClientTest();
    private StompClientConnection connection;

    public static void init() {
        Vertx vertx = Vertx.vertx();
        StompClientOptions options = new StompClientOptions();
        options.setConnectTimeout(60000);
        options.setIdleTimeout(90000);
        options.setLogin("D011");
        options.setPasscode("D011");

        StompClient stompClient = StompClient.create(vertx, options);
        stompClient.receivedFrameHandler(event -> {
            System.out.println("Client ReceivedFrameHandler: " + event.getCommand().name());
        });

        int brokerPort = 61613;
        String brokerHost = "10.211.55.3";
        String topic1 = "/topic/jms.samples.chat";
        String topic2 = "/topic/custom.topic";
        String queue1 = "/queue/SampleQ1";
        String queue2 = "/queue/SampleQ2";

        StompClientConnection[] connectionHolder = new StompClientConnection[1];
        while (connectionHolder[0] == null) {
            stompClient.connect(brokerPort, brokerHost, ar -> {
                if (ar.succeeded()) {
                    connectionHolder[0] = ar.result();
                    System.out.println("* Ready to send STOMP frames");
                } else {
                    System.out.println("* Failed to connect to the STOMP server: " + ar.cause().toString());
                    stompClient.close();
                    vertx.close();
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }

        client.setConnection(connectionHolder[0]);

        client.connection.errorHandler(event -> {
            System.out.println("< Error: " + event.getCommand().name()
                    + " hh: " + event.getHeaders().toString()
                    + " pl: " + event.getBodyAsString(StandardCharsets.UTF_8.name()));
        });

        client.connection.closeHandler(event -> {
            System.out.println("* Close: " + event.toString());
        });

        client.connection.receivedFrameHandler(event -> {
            System.out.println("< Recvd: " + event.getCommand().name());
            System.out.println("  - hh: " + event.getHeaders().toString());
            System.out.println("  - pl: " + event.getBodyAsString());
        });

        client.connection.connectionDroppedHandler(event -> {
            System.out.println("< Recvd: CONNECTION DROPPED");
            stompClient.close();
            vertx.close();
        });

        client.connection.pingHandler(event -> {
            System.out.println("* Pings: " + event.toString());
        });

        client.connection.writingFrameHandler(event -> {
            System.out.println("> Emit:: " + event.getCommand().toString()
                    + " hh: " + event.getHeaders().toString());
        });

        client.subscribe(topic1, "topic1|auto|1");
        client.subscribe(topic1, "topic1|auto|2");

        String msg = "Hello!!! " + new Date().toString();
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("custom-header", "custom-value1");
        client.send(topic1, msg, customHeaders);

        //System.out.println("* Client initiates disconnect");
        //connection.disconnect();

    }

    public static void main(String[] args) {
        init();
    }

    public void send(String destination, String textMessage, Map<String, String> customHeaders) {
        Map<String, String> headers = new HashMap<>();
        if (customHeaders != null && !customHeaders.isEmpty()) {
            headers.putAll(customHeaders);
        }
        Buffer payload = Buffer.buffer(textMessage);
        connection.send(destination, headers, payload, event -> {
            System.out.println("Send. Receipt handler: " + event.getBodyAsString("UTF-8"));
        });
    }

    public StompClientConnection getConnection() {
        return connection;
    }

    public void setConnection(StompClientConnection connection) {
        this.connection = connection;
    }

    public void subscribe(String destination, String subscriptionId) {
        //1. Subscribe on TOPIC with AUTO ack mode:
        Map<String, String> headers = new HashMap<>();
        if (subscriptionId != null) {
            headers.put("id", subscriptionId);
        }
        connection.subscribe(destination, headers,
                event -> {
                    System.out.println("> Subscription. Received handler: "
                            + event.getCommand().toString()
                            + ": " + event.getBodyAsString());
                },
                event -> {
                    System.out.println("> Subscription. Receipt handler: " + event.getCommand().toString());
                }
        );

    }
}
