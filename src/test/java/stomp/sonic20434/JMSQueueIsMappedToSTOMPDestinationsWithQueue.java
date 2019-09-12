package stomp.sonic20434;

import stomp.VertxStompClient;

public class JMSQueueIsMappedToSTOMPDestinationsWithQueue {

    private static String queueToSubscribeOn = "/queue/s1";
    private static String subscriptionId = "subscription-1";

    public static void main(String[] args) {
        VertxStompClient client = new VertxStompClient("10.211.55.3", 61613, "D01", "D01");


        client.connect();
        client.subscribeAutoAck(queueToSubscribeOn, subscriptionId, event -> {
            System.out.println("< " + event.getCommand().name() + " on SUBSCRIPTION [" + subscriptionId + "]: "
                    + " hh: " + event.getHeaders().toString()
                    + " pl: " + event.getBodyAsString());
        });
    }
}
