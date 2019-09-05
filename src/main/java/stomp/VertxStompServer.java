package stomp;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.stomp.Frame;
import io.vertx.ext.stomp.StompClientConnection;
import io.vertx.ext.stomp.StompServer;
import io.vertx.ext.stomp.StompServerHandler;
import io.vertx.ext.stomp.StompServerOptions;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VertxStompServer {
    StompServerOptions options = new StompServerOptions();
    private StompServer stompServer;
    private Vertx vertx = Vertx.vertx();
    //new VertxOptions().setAddressResolverOptions(new AddressResolverOptions().setHostsValue(Buffer.buffer("10.211
    // .55.3 myserver"))));
    private StompServerHandler handler;
    private String listenHost = "0.0.0.0";
    private int listenPort;

    public VertxStompServer(int listenPort) {

        this.listenPort = listenPort;

        options.setIdleTimeout(60000);

        this.handler = StompServerHandler.create(vertx);
        this.stompServer = StompServer
                .create(vertx, options)
                .handler(handler);
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
