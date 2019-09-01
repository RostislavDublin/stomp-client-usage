package stomp.sonic21231;

import other.Helper;
import stomp.VertxStompClient;

public class AllowHierarchicalSTOMPDestinationsByUsingTheForwardSlashAsSeparator {

    private static String client1_subscriptionId = "client1-subscription-1";
    private static String client1_topicToSubscribeOn = "/topic/L1";

    private static String client2_subscriptionId = "client2-subscription-2";
    private static String client2_topicToSubscribeOn = "/topic/L1/L2";


    public static void main(String[] args) {
        VertxStompClient client1 = new VertxStompClient("10.211.55.3", 61613, "D01", "D01").connect();
        client1.subscribeAutoAck(client1_topicToSubscribeOn, client1_subscriptionId, event -> {
            System.out.println("< " + event.getCommand().name() + " on SUBSCRIPTION [" + client1_subscriptionId + "]: "
                    + " hh: " + event.getHeaders().toString() + " pl: " + event.getBodyAsString());
        });


        VertxStompClient client2 = new VertxStompClient("10.211.55.3", 61613, "D01", "D01").connect();
        client2.subscribeAutoAck(client2_topicToSubscribeOn, client2_subscriptionId, event -> {
            System.out.println("< " + event.getCommand().name() + " on SUBSCRIPTION [" + client2_subscriptionId + "]: "
                    + " hh: " + event.getHeaders().toString() + " pl: " + event.getBodyAsString());
        });

        VertxStompClient client3 = new VertxStompClient("10.211.55.3", 61613, "D01", "D01").connect();

        client3.send(client1_topicToSubscribeOn, "This is for topic ["+client1_topicToSubscribeOn+"] only.");
        Helper.sleep(10000);

        client3.send(client2_topicToSubscribeOn, "This is for topic ["+client2_topicToSubscribeOn+"] only.");
        Helper.sleep(10000);

        client1.close();
        client2.close();
        client3.close();
    }


}
