package stomp;

import io.netty.util.internal.StringUtil;
import other.Helper;
import other.Say;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TransactiveQueueTalk {
    private static Say say = new Say();
    private static String subscription1 = "sub-1";
    private static String subscription2 = "sub-2";
    private static String queue1 = "/queue/SampleQ1";
    private static String queue2 = "/queue/SampleQ2";

    public static void main(String[] args) {
        VertxStompClient client = new VertxStompClient("10.211.55.3", 61613, "D01", "D01");
        client.setReceiptHandler(null);
        client.connect();

        client.subscribeAutoAck(queue1, subscription1, event -> {
            System.out.println("< " + event.getCommand().name() + " on SUBSCRIPTION [" + subscription1 + "]: "
                    + " pl: " + event.getBodyAsString() + " hh: " + event.getHeaders().toString()
            );
        });
        client.subscribeAutoAck(queue2, subscription2, event -> {
            System.out.println("< " + event.getCommand().name() + " on SUBSCRIPTION [" + subscription2 + "]: "
                    + " pl: " + event.getBodyAsString() + " hh: " + event.getHeaders().toString()
            );
        });

        client.send(queue1, "msg0-noTX");
        client.getConnection().beginTX("T1");
        client.getConnection().beginTX("T2");
        client.getConnection().beginTX("T3");
        client.send(queue2, "msg1-T3", Collections.singletonMap("transaction", "T3"));
        client.send(queue2, "msg1-T2", Collections.singletonMap("transaction", "T2"));
        client.send(queue1, "msg1-T1", Collections.singletonMap("transaction", "T1"));
        client.send(queue1, "msg1-noTX");
        client.send(queue2, "msg2-noTX");
        client.send(queue2, "msg2-T1", Collections.singletonMap("transaction", "T1"));
        client.send(queue2, "msg1-T2", Collections.singletonMap("transaction", "T2"));
        Helper.sleep(5000);
        client.getConnection().commit("T1");
        Helper.sleep(5000);
        client.getConnection().commit("T2");
        Helper.sleep(5000);
        client.getConnection().abort("T3");
        Helper.sleep(5000);
        client.close();
    }
}

