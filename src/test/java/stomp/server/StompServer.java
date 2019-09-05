package stomp.server;

import stomp.VertxStompServer;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StompServer {
    public static void main(String[] args) {

        VertxStompServer server = new VertxStompServer(61613);

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

