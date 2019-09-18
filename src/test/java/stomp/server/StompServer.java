package stomp.server;

import io.vertx.ext.stomp.Frame;
import stomp.VertxStompServer;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StompServer {
    public static void main(String[] args) {

        VertxStompServer server = new VertxStompServer(61613);

        server.getStompServer().stompHandler()
              .receivedFrameHandler((serverFrame) -> {
                  System.out.println("<<< " + serverFrame.frame().toString());
              })
              .onAckHandler(acknowledgement -> {
                  // Action to execute when the frames (one in `client-individual` mode, several
                  // in `client` mode are acknowledged.
                  Frame subscription = acknowledgement.subscription();
                  System.out.println("Raised event ACK on subscription" + subscription.getId());
                  System.out.println("- messages acknowledged: " + acknowledgement.frames().toString());
              })
              .onNackHandler(acknowledgement -> {
                  // Action to execute when the frames (1 in `client-individual` mode, several in
                  // `client` mode are not acknowledged.
                  Frame subscription = acknowledgement.subscription();
                  System.out.println("Raised event NACK on subscription" + subscription.getId());
                  System.out.println("- messages NOT acknowledged: " + acknowledgement.frames().toString());
              });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            server.start();
        });
        executor.shutdown();

        Scanner in = new Scanner(System.in);
        while (true) {
            String msg = in.nextLine();
            server.stop();
            break;
        }
    }
}

