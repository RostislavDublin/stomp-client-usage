package stomp;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.stomp.Frame;
import io.vertx.ext.stomp.StompClient;
import io.vertx.ext.stomp.StompClientConnection;
import io.vertx.ext.stomp.StompClientOptions;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VertxStompClient {
    private static String topic1 = "/topic/jms.samples.chat";
    private static String topic2 = "/topic/custom.topic";
    private static String queue1 = "/queue/SampleQ1";
    private static String queue2 = "/queue/SampleQ2";
    private static String queue3 = "/queue/SampleQ3";
    final boolean[] stopAttempts = new boolean[1];
    StompClientOptions options = new StompClientOptions();
    private StompClient stompClient;
    private Vertx vertx = Vertx.vertx();
    //new VertxOptions().setAddressResolverOptions(new AddressResolverOptions().setHostsValue(Buffer.buffer("10.211
    // .55.3 myserver"))));
    private StompClientConnection connection;
    private int brokerPort;
    private String brokerHost;
    private Runnable closedHandler = null;

    public VertxStompClient(String brokerHost, int brokerPort, String login, String password) {

        this.brokerHost = brokerHost;
        this.brokerPort = brokerPort;

        options.setConnectTimeout(10000);
        options.setIdleTimeout(60000);
        options.setLogin(login);
        options.setPasscode(password);

        stompClient = StompClient.create(vertx, options);

        stompClient.errorFrameHandler(event -> {
            System.out.println("< Connection process: " + event.getCommand().name()
                    + " hh: " + event.getHeaders().toString()
                    + " pl: " + event.getBodyAsString(StandardCharsets.UTF_8.name()));
            stopAttempts[0] = true;
            close();
        });

    }

    public StompClientOptions getOptions() {
        return options;
    }

    public void send(String destination, String textMessage) {
        send(destination, textMessage, null);
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

    public void subscribeAutoAck(String destination, String subscriptionId, Handler<Frame> handler) {
        subscribe(destination, subscriptionId, "auto", handler);
    }

    public void subscribeAutoAck(String destination, String subscriptionId) {
        subscribe(destination, subscriptionId, "auto");
    }

    public void subscribe(String destination, String subscriptionId, String ackMode) {
        subscribe(destination, subscriptionId, ackMode, event -> {
            System.out.println("< " + event.getCommand().name() + " on SUBSCRIPTION [" + subscriptionId + "]: "
                    + " hh: " + event.getHeaders().toString()
                    + " pl: " + event.getBodyAsString());

            if (ackMode != "auto" && !event.getHeaders().containsKey("noAck")) {
                String ack = event.getAck();
                connection.ack(ack);
            }
        });
    }

    public void subscribe(String destination, String subscriptionId, String ackMode, Handler<Frame> handler) {
        //1. Subscribe on TOPIC with AUTO ack mode:
        Map<String, String> headers = new HashMap<>();
        if (subscriptionId != null) {
            headers.put("id", subscriptionId);
        }
        if (ackMode != null) {
            headers.put("ack", ackMode);
        }
        connection.subscribe(destination, headers, handler,
                event -> {
                    System.out.println("< RECEIPT on SUBSCRIBE [" + event.getReceipt() + "]");
                }
        );

    }

    public VertxStompClient connect() {
        final StompClientConnection[] connectionHolder = new StompClientConnection[1];
        stopAttempts[0] = false;
        System.out.println("* CONNECT...");

        stompClient.connect(brokerPort, brokerHost, ar -> {
            if (ar.succeeded()) {
                connectionHolder[0] = ar.result();
                System.out.println("* CONNECTED Ready to send STOMP frames");
            } else {
                if(!stopAttempts[0]) {
                    stopAttempts[0] = true;
                    System.out.println("* FAILED to connect to the STOMP server: " + ar.cause().toString());
                }
                stompClient.close();
                vertx.close();
            }
        });

        while ((connectionHolder[0] == null) && !stopAttempts[0]) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }

        if (stopAttempts[0]) {
            return this;
        }

        setConnection(connectionHolder[0]);

        connection.errorHandler(event -> {
            System.out.println("< Error: " + event.getCommand().name()
                    + " hh: " + event.getHeaders().toString()
                    + " pl: " + event.getBodyAsString(StandardCharsets.UTF_8.name()));
        });

        connection.closeHandler(event -> {
            System.out.println("* DISCONNECTED");
            close();
        });

/*
        client.connection.receivedFrameHandler(event -> {
            System.out.println("< Recvd: " + event.getCommand().name()
            + " hh: " + event.getHeaders().toString()
            + " pl: " + event.getBodyAsString());
        });
*/

        connection.connectionDroppedHandler(event -> {
            System.out.println("< Recvd: CONNECTION DROPPED");
            close();
        });

        connection.pingHandler(event -> {
            System.out.println("* Pings: " + event.toString());
        });

        connection.writingFrameHandler(event -> {
            System.out.println("> " + event.getCommand().toString()
                    + " hh: " + event.getHeaders().toString()
            );
        });

        stompClient.receivedFrameHandler(event -> {
            System.out.println("Client ReceivedFrameHandler: " + event.getCommand().name());
        });

        return this;
    }



    public void close() {
        if (getConnection() != null && getConnection().isConnected()) {
            System.out.println("DISCONNECT");
            connection.disconnect();
        }
        stompClient.close();
        vertx.close();

        if(closedHandler != null){
            closedHandler.run();
        }
    }

    public boolean isConnected() {
        return getConnection() != null && getConnection().isConnected();
    }

    public void setClosedHandler(Runnable closedHandler) {
        this.closedHandler = closedHandler;
    }
}
