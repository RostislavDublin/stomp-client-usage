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
    private StompClient stompClient;
    private Vertx vertx = Vertx.vertx();
    private StompClientConnection connection;

    private int brokerPort = 61613;
    private String brokerHost = "10.211.55.3";
    private static String topic1 = "/topic/jms.samples.chat";
    private static String topic2 = "/topic/custom.topic";
    private static String queue1 = "/queue/SampleQ1";
    private static String queue2 = "/queue/SampleQ2";
    private static String queue3 = "/queue/SampleQ3";

    public VertxStompClientTest() {
        StompClientOptions options = new StompClientOptions();
        options.setConnectTimeout(60000);
        options.setIdleTimeout(60*10);
        options.setLogin("D011");
        options.setPasscode("D011");

        stompClient = StompClient.create(vertx, options);
    }

    public static void init() {

        String msg;
        Map<String, String> customHeaders = new HashMap<>();

        //client.subscribeAutoAck(topic1, "topic1|auto|1");
        //client.subscribe(topic1, "topic1|client|2", "client");
        //client.subscribe(topic1, "topic1|cl-ind|3", "client-individual");
        ///sleep(1000);

//        msg = "Hello TOPIC: " + new Date().toString();
//        customHeaders.put("custom1", "value1");
//        client.send(topic1, msg, customHeaders);
//        sleepALittle(2000);

        client.connect();

        boolean testQueueAckAuto = true;
        if (testQueueAckAuto) {
            System.out.println("TEST QUEUE ack:AUTO");
            client.subscribeAutoAck(queue1, "queue1|auto|1");
            client.subscribeAutoAck(queue1, "queue1|auto|2");
            client.subscribeAutoAck(queue1, "queue1|auto|3");
/*
            while (true) {
                sleep(10000);
                customHeaders.put("header1", "value1" + new Date().toString());
                customHeaders.put("header2", "value2" + new Date().toString());
                msg = "Hello QUEUE 1.1: " + new Date().toString();
                client.send(queue1, msg, new HashMap<>(customHeaders));
            }
*/
            msg = "Hello QUEUE 1.1: " + new Date().toString();
            client.send(queue1, msg, new HashMap<>(customHeaders));

            msg = "Hello QUEUE 1.2: " + new Date().toString();
            client.send(queue1, msg, new HashMap<>(customHeaders));
            msg = "Hello QUEUE 1.3: " + new Date().toString();
            client.send(queue1, msg, new HashMap<>(customHeaders));
            msg = "Hello QUEUE 1.4: " + new Date().toString();
            client.send(queue1, msg, new HashMap<>(customHeaders));
            sleep(1000);
            client.connection.unsubscribe("queue1|auto|2");
            client.connection.unsubscribe("queue1|auto|3");
            sleep(1000);
            msg = "Hello QUEUE 1.5: " + new Date().toString();
            client.send(queue1, msg, new HashMap<>(customHeaders));
            msg = "Hello QUEUE 1.6: " + new Date().toString();
            client.send(queue1, msg, new HashMap<>(customHeaders));
            sleep(1000);
            client.connection.unsubscribe("queue1|auto|1");
            sleep(1000);
        }

        boolean testQueueAckClient = true;
        if (testQueueAckClient) {
            System.out.println("TEST QUEUE ack:CLIENT");
            client.subscribe(queue2, "queue2|client|1", "client");
            sleep(1000);
            customHeaders.put("noAck", "noAck");
            msg = "Hello QUEUE 2.1: " + new Date().toString();
            client.send(queue2, msg, new HashMap<>(customHeaders));
            msg = "Hello QUEUE 2.2: " + new Date().toString();
            client.send(queue2, msg, new HashMap<>(customHeaders));
            sleep(1000);
            client.connection.disconnect();
            sleep(1000);
            client.connect();
            client.subscribe(queue2, "queue2|client|1", "client");
            sleep(1000);
            customHeaders.remove("noAck");
            msg = "Hello QUEUE 2.3: " + new Date().toString();
            client.send(queue2, msg, new HashMap<>(customHeaders));
            sleep(1000);
        }

//        System.out.println("TEST QUEUE CLIENT-INDIVIDUAL");
//        client.subscribe(queue3, "queue1|cl-ind|3", "client-individual");
//        sleep(1000);
//        msg = "Hello QUEUE 3: " + new Date().toString();
//        client.send(queue3, msg, new HashMap<>(customHeaders));
//        msg = "Hello QUEUE 3: " + new Date().toString();
//        client.send(queue3, msg, new HashMap<>(customHeaders));
//        customHeaders.remove("noAck");
//        msg = "Hello QUEUE 3: " + new Date().toString();
//        client.send(queue3, msg, new HashMap<>(customHeaders));


//        System.out.println("* Client initiates disconnect");
//        client.connection.disconnect();

    }

    public static void sleep(int howLongToSleep) {
        System.out.println("...sleep a little........................");
        try {
            Thread.sleep(howLongToSleep);
        } catch (InterruptedException e) {
            ///e.printStackTrace();
        }
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
            System.out.println("< RECEIPT on SEND [" + event.getReceipt() + "] ");
        });
    }

    public StompClientConnection getConnection() {
        return connection;
    }

    public void setConnection(StompClientConnection connection) {
        this.connection = connection;
    }

    public void subscribeAutoAck(String destination, String subscriptionId) {
        subscribe(destination, subscriptionId, "auto");
    }

    public void subscribe(String destination, String subscriptionId, String ackMode) {
        //1. Subscribe on TOPIC with AUTO ack mode:
        Map<String, String> headers = new HashMap<>();
        if (subscriptionId != null) {
            headers.put("id", subscriptionId);
        }
        if (ackMode != null) {
            headers.put("ack", ackMode);
        }
        connection.subscribe(destination, headers,
                event -> {
                    System.out.println("< " + event.getCommand().name() + " on SUBSCRIPTION [" + subscriptionId + "]: "
                            + " hh: " + event.getHeaders().toString()
                            + " pl: " + event.getBodyAsString());

                    if (ackMode != "auto" && !event.getHeaders().containsKey("noAck")) {
                        String ack = event.getAck();
                        connection.ack(ack);
                    }
                },
                event -> {
                    System.out.println("< RECEIPT on SUBSCRIBE [" + event.getReceipt() + "]");
                }
        );

    }

    public void connect() {
        StompClientConnection[] connectionHolder = new StompClientConnection[1];
        while (connectionHolder[0] == null) {
            System.out.println("* CONNECT...");
            stompClient.connect(brokerPort, brokerHost, ar -> {
                if (ar.succeeded()) {
                    connectionHolder[0] = ar.result();
                    System.out.println("* CONNECTED Ready to send STOMP frames");
                } else {
                    System.out.println("* FAILED to connect to the STOMP server: " + ar.cause().toString());
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
            System.out.println("* DISCONNECTED");
        });

/*
        client.connection.receivedFrameHandler(event -> {
            System.out.println("< Recvd: " + event.getCommand().name()
            + " hh: " + event.getHeaders().toString()
            + " pl: " + event.getBodyAsString());
        });
*/

        client.connection.connectionDroppedHandler(event -> {
            System.out.println("< Recvd: CONNECTION DROPPED");
            stompClient.close();
            vertx.close();
        });

        client.connection.pingHandler(event -> {
            System.out.println("* Pings: " + event.toString());
        });

        client.connection.writingFrameHandler(event -> {
            System.out.println("> " + event.getCommand().toString()
                    + " hh: " + event.getHeaders().toString()
            );
        });

/*
        stompClient.receivedFrameHandler(event -> {
            System.out.println("Client ReceivedFrameHandler: " + event.getCommand().name());
        });
*/
    }
}
