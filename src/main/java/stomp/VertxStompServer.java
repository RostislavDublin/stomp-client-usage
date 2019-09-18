package stomp;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.stomp.Destination;
import io.vertx.ext.stomp.StompServer;
import io.vertx.ext.stomp.StompServerHandler;
import io.vertx.ext.stomp.StompServerOptions;

public class VertxStompServer {
    StompServerOptions options = new StompServerOptions();
    private StompServer stompServer;
    private Vertx vertx = Vertx.vertx();
    private StompServerHandler handler;
    private String listenHost = "0.0.0.0";
    private int listenPort;

    public VertxStompServer(int listenPort) {

        this.listenPort = listenPort;

        options.setIdleTimeout(60000);
        options.setHeartbeat(new JsonObject().put("x", 60000).put("y", 60000));

        handler = StompServerHandler.create(vertx);
        handler.destinationFactory((v, name) -> {
            if (name.startsWith("/queue")) {
                System.out.println("Registered QUEUE destination: " + name);
                return Destination.queue(vertx, name);
            } else {
                System.out.println("Registered TOPIC destination: " + name);
                return Destination.topic(vertx, name);
            }
        });
        stompServer = StompServer
                .create(vertx, options)
                .handler(handler)
                .writingFrameHandler((serverFrame) -> {
                    System.out.println(">>> " + serverFrame.frame().toString());
                });
    }

    public VertxStompServer start() {

        System.out.println("* STARTING...");

        stompServer.listen(listenPort, listenHost, ar -> {
            if (ar.failed()) {
                System.out.println("Failing to start the STOMP server : " + ar.cause().getMessage());
            } else {
                System.out.println("Ready to receive STOMP frames");
            }
        });
        return this;
    }

    public StompServerOptions getOptions() {
        return options;
    }

    public void stop() {
        if (stompServer.isListening()) {
            stompServer.close(ar -> {
                if (ar.succeeded()) {
                    System.out.println("The STOMP server has been closed");
                } else {
                    System.out.println("The STOMP server failed to close : " + ar.cause().getMessage());
                }
            });
        }
        vertx.close();
    }

    public StompServer getStompServer() {
        return stompServer;
    }
}
