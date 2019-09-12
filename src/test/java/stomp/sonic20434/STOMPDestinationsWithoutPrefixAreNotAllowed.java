package stomp.sonic20434;

import stomp.VertxStompClient;

public class STOMPDestinationsWithoutPrefixAreNotAllowed {
    private static String client_destination1 = "test1";

    public static void main(String[] args) {
        VertxStompClient client = new VertxStompClient("10.211.55.3", 61613, "D01", "D01");

        client.connect();
        client.send(client_destination1, "This is for queue ["+ client_destination1 +"] subscribers.");
    }
}
