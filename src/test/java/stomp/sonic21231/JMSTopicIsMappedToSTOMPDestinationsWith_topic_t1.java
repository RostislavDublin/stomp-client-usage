package stomp.sonic21231;

import stomp.VertxStompClient;

public class JMSTopicIsMappedToSTOMPDestinationsWith_topic_t1 {

    private static String topicToSubscribeOn = "/topic/t1";
    private static String subscriptionId = "subscription-1";

    public static void main(String[] args) {
        VertxStompClient client = new VertxStompClient("10.211.55.3", 61613, "D01", "D01");


        client.connect();
        client.subscribeAutoAck(topicToSubscribeOn, subscriptionId, event -> {
            System.out.println("< " + event.getCommand().name() + " on SUBSCRIPTION [" + subscriptionId + "]: "
                    + " hh: " + event.getHeaders().toString()
                    + " pl: " + event.getBodyAsString());
        });
    }
}
