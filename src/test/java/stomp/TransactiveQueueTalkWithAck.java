package stomp;

import other.Helper;
import other.Say;

import java.util.Collections;

public class TransactiveQueueTalkWithAck {
    private static Say say = new Say();
    private static String subscription1 = "sub-1";
    private static String queue1 = "/queue/SampleQ1";

    public static void main(String[] args) {
        VertxStompClient client = new VertxStompClient("10.211.55.3", 61613, "D01", "D01");
        client.setReceiptHandler(null);
        client.connect();

        client.getConnection().beginTX("T1");

        client.subscribe(queue1, subscription1, "client", event -> {
            System.out.println("< " + event.getCommand().name() + " on SUBSCRIPTION [" + subscription1 + "]: "
                    + " pl: " + event.getBodyAsString() + " hh: " + event.getHeaders().toString()
            );
            String ack = event.getHeader("ack");
            client.getConnection().ack(ack, "T1");
        });

        client.send(queue1, "msg1-noTX");
        Helper.sleep(5000);
        client.getConnection().commit("T1");
        Helper.sleep(1000);
        client.close();
    }
}

