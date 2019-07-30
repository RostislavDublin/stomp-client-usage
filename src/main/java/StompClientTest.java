import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.stomp.StompClient;
import io.vertx.ext.stomp.StompClientConnection;
import io.vertx.ext.stomp.StompClientOptions;

import java.nio.charset.StandardCharsets;

public class StompClientTest {

    public static void init() {
        Vertx vertx = Vertx.vertx();
        StompClientOptions options = new StompClientOptions();
        options.setConnectTimeout(60000);
        options.setIdleTimeout(90000);
        options.setLogin("Administrator");
        options.setPasscode("Administrator");

        StompClient client = StompClient.create(vertx, options);
        client.receivedFrameHandler(event -> {
            System.out.println("Client ReceivedFrameHandler: " + event.getCommand().name());
        });

        client.connect(61613, "172.30.94.196", ar -> {
            //client.connect(61613, "127.0.0.1", ar -> {
            if (ar.succeeded()) {
                StompClientConnection connection = ar.result();
                System.out.println("* Ready to send STOMP frames");

                connection.errorHandler(event -> {
                    System.out.println("< Error: " + event.getCommand().name()
                            + " hh: " + event.getHeaders().toString()
                            + " pl: " + event.getBodyAsString(StandardCharsets.UTF_8.name()));
                });

                connection.closeHandler(event -> {
                    System.out.println("* Close: " + event.toString());
                });

                connection.receivedFrameHandler(event -> {
                    System.out.println("< Recvd: " + event.getCommand().name());
                    System.out.println("  - hh: " + event.getHeaders().toString());
                    System.out.println("  - pl: " + event.getBodyAsString());
                });

                connection.connectionDroppedHandler(event -> {
                    System.out.println("< Recvd: CONNECTION DROPPED");
                    client.close();
                    vertx.close();
                });

                connection.pingHandler(event -> {
                    System.out.println("* Pings: " + event.toString());
                });

                connection.writingFrameHandler(event -> {
                    System.out.println("> Sent:: " + event.getCommand().toString()
                            + " hh: " + event.getHeaders().toString());
                });

                //connection.subscribe("Sample/Q1",
                connection.subscribe("jms.samples.chat",
                        event -> { System.out.println("> Subscription. Received handler: "
                                + event.getCommand().toString()
                                +": "+event.getBodyAsString()); },
                        event -> { System.out.println("> Subscription. Receipt handler "   + event.getCommand().toString()); }
                        );

                String msg = "Hello Dolly!";
                Buffer payload = Buffer.buffer(msg);
                connection.send("jms.samples.chat", payload, event -> {
                    System.out.println("<> ??? " + event.getBodyAsString("UTF-8"));
                });

                //System.out.println("* Client initiates disconnect");
                //connection.disconnect();

            } else {
                System.out.println("* Failed to connect to the STOMP server: " + ar.cause().toString());
                client.close();
                vertx.close();
            }
        });
    }

    public static void main(String[] args) {
        init();
    }
}
