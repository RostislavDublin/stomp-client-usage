package stomp;

import org.junit.Test;
import other.Helper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BigTest {

    private static String topic1 = "/topic/jms.samples.chat";
    private static String topic2 = "/topic/custom.topic";
    private static String queue1 = "/queue/SampleQ1";
    private static String queue2 = "/queue/SampleQ2";
    private static String queue3 = "/queue/SampleQ3";
    private VertxStompClient client;

    public BigTest() {
        client = new VertxStompClient("10.211.55.3", 61613, "D01", "D01");
    }

    @Test
    public void test() {

        String msg;
        Map<String, String> customHeaders = new HashMap<>();

        //client.subscribeAutoAck(topic1, "topic1|auto|1");
        //client.subscribe(topic1, "topic1|client|2", "client");
        //client.subscribe(topic1, "topic1|cl-ind|3", "client-individual");
        ///sleep(1000);

//        msg = "Hello TOPIC: " + new Date().toString();
//        customHeaders.put("custom1", "value1");
//        client.send(topic1, msg, customHeaders);
//        sleepALittle(2000);

        client.connect();

        boolean testQueueAckAuto = true;
        if (testQueueAckAuto) {
            System.out.println("TEST QUEUE ack:AUTO");
            client.subscribeAutoAck(queue1, "queue1|auto|1");
            client.subscribeAutoAck(queue1, "queue1|auto|2");
            client.subscribeAutoAck(queue1, "queue1|auto|3");
/*
            while (true) {
                sleep(10000);
                customHeaders.put("header1", "value1" + new Date().toString());
                customHeaders.put("header2", "value2" + new Date().toString());
                msg = "Hello QUEUE 1.1: " + new Date().toString();
                client.send(queue1, msg, new HashMap<>(customHeaders));
            }
*/
            msg = "Hello QUEUE 1.1: " + new Date().toString();
            client.send(queue1, msg, new HashMap<>(customHeaders));

            msg = "Hello QUEUE 1.2: " + new Date().toString();
            client.send(queue1, msg, new HashMap<>(customHeaders));
            msg = "Hello QUEUE 1.3: " + new Date().toString();
            client.send(queue1, msg, new HashMap<>(customHeaders));
            msg = "Hello QUEUE 1.4: " + new Date().toString();
            client.send(queue1, msg, new HashMap<>(customHeaders));
            Helper.sleep(1000);
            client.getConnection().unsubscribe("queue1|auto|2");
            client.getConnection().unsubscribe("queue1|auto|3");
            Helper.sleep(1000);
            msg = "Hello QUEUE 1.5: " + new Date().toString();
            client.send(queue1, msg, new HashMap<>(customHeaders));
            msg = "Hello QUEUE 1.6: " + new Date().toString();
            client.send(queue1, msg, new HashMap<>(customHeaders));
            Helper.sleep(1000);
            client.getConnection().unsubscribe("queue1|auto|1");
            Helper.sleep(1000);
        }

        boolean testQueueAckClient = true;
        if (testQueueAckClient) {
            System.out.println("TEST QUEUE ack:CLIENT");
            client.subscribe(queue2, "queue2|client|1", "client");
            Helper.sleep(1000);
            customHeaders.put("noAck", "noAck");
            msg = "Hello QUEUE 2.1: " + new Date().toString();
            client.send(queue2, msg, new HashMap<>(customHeaders));
            msg = "Hello QUEUE 2.2: " + new Date().toString();
            client.send(queue2, msg, new HashMap<>(customHeaders));
            Helper.sleep(1000);
            client.getConnection().disconnect();
            Helper.sleep(1000);
            client.connect();
            client.subscribe(queue2, "queue2|client|1", "client");
            Helper.sleep(1000);
            customHeaders.remove("noAck");
            msg = "Hello QUEUE 2.3: " + new Date().toString();
            client.send(queue2, msg, new HashMap<>(customHeaders));
            Helper.sleep(1000);
        }

        client.getConnection().disconnect();

//        System.out.println("TEST QUEUE CLIENT-INDIVIDUAL");
//        client.subscribe(queue3, "queue1|cl-ind|3", "client-individual");
//        sleep(1000);
//        msg = "Hello QUEUE 3: " + new Date().toString();
//        client.send(queue3, msg, new HashMap<>(customHeaders));
//        msg = "Hello QUEUE 3: " + new Date().toString();
//        client.send(queue3, msg, new HashMap<>(customHeaders));
//        customHeaders.remove("noAck");
//        msg = "Hello QUEUE 3: " + new Date().toString();
//        client.send(queue3, msg, new HashMap<>(customHeaders));


//        System.out.println("* Client initiates disconnect");
//        client.connection.disconnect();

    }
}
