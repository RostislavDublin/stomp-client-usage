package stomp.sonic21231;

import other.Helper;
import stomp.VertxStompClient;

public class UseWildcardSymbolsPoundAndStar {

    private static String client1_subscriptionId = "client1-subscription-1";
    private static String client1_topicToSubscribeOn = "/topic/#";

    private static String client2_subscriptionId = "client2-subscription-2";
    private static String client2_topicToSubscribeOn = "/topic/start/*";

    private static String client3_subscriptionId = "client3-subscription-3";
    private static String client3_topicToSubscribeOn = "/topic/*";

    private static String client4_destination1 = "/topic/start/alpha";
    private static String client4_destination2 = "/topic/start/gamma";
    private static String client4_destination3 = "/topic/theta";
    private static String client4_destination4 = "/topic/end/omega";


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
        client2.subscribeAutoAck(client3_topicToSubscribeOn, client3_subscriptionId, event -> {
            System.out.println("< " + event.getCommand().name() + " on SUBSCRIPTION [" + client3_subscriptionId + "]: "
                    + " hh: " + event.getHeaders().toString() + " pl: " + event.getBodyAsString());
        });

        VertxStompClient client4 = new VertxStompClient("10.211.55.3", 61613, "D02", "D02").connect();

        client4.send(client4_destination1, "This is for topic ["+client4_destination1+"] subscribers.");
        Helper.sleep(10000);

        client4.send(client4_destination2, "This is for topic ["+client4_destination2+"] subscribers.");
        Helper.sleep(10000);

        client4.send(client4_destination3, "This is for topic ["+client4_destination3+"] subscribers.");
        Helper.sleep(10000);

        client4.send(client4_destination4, "This is for topic ["+client4_destination4+"] subscribers.");
        Helper.sleep(10000);

        client1.close();
        client2.close();
        client3.close();
        client4.close();
    }


}
