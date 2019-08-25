package stomp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import other.Helper;
import other.Say;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConvertStompToJmsAndBack {
    private static Say say = new Say();
    private static String queue1 = "/queue/SampleQ1";
    private static String subscriptionId = "queue1|auto|1";

    private static String customHeader1_name = "custom-header-1";
    private static String customHeader1_value = "custom-value-1";
    private static String customHeader2_name = "custom-header-2";
    private static String customHeader2_value = "custom-value-2";
    private VertxStompClient client;
    private String msg = "Hello QUEUE: " + new Date().toString();

    @Before
    public void beforeClass() {
        say.addln("This test class is for SONIC-20432. Vertx STOMP client connects to the broker")
                .addln("subscribes to the SampleQ1 queue with AUTO acknowledge mode")
                .addln("and sends a STOMP 'SEND' message (with custom text and headers) into it.")
                .addln("The message comes to the broker's STOMP acceptor, converts to the JMS and goes to the SampleQ1 queue.")
                .addln("The queue sends JMS message back to the subscriber, it gets to the STOMP acceptor's internal JMS client,")
                .addln("converts to the STOMP 'MESSAGE' message and sends back to the Vertx STOMP client.")
                .addln("STOMP client sends 'DISCONNECT'.")
                .addln("")
                .addln("Before the test, set the following Broker advanced property:")
                .addln(" - name: DEBUG_PARAMETERS.DEBUG_NAME")
                .addln(" - value: StompSender:65536;StompListener:65536;StompAgentListener:65536;StompAgentSender:65536;StompSubscriptionHandler:65536")
                .addln("to see STOMP/JMS messages exchange through the Broker")
                .out();

        client = new VertxStompClient("10.211.55.3", 61613, "D01", "D01");
    }

    @Test
    public void convertStompToJmsAndBack() {

        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put(customHeader1_name, customHeader1_value);
        customHeaders.put(customHeader2_name, customHeader2_value);

        client.connect();

        io.vertx.ext.stomp.Frame[] messageFrame = new io.vertx.ext.stomp.Frame[1];

        client.subscribeAutoAck(queue1, subscriptionId, event -> {
            System.out.println("< " + event.getCommand().name() + " on SUBSCRIPTION [" + subscriptionId + "]: "
                    + " hh: " + event.getHeaders().toString()
                    + " pl: " + event.getBodyAsString());

            messageFrame[0] = event;
            Helper.notifyAll(messageFrame);
        });

        client.send(queue1, msg, new HashMap<>(customHeaders));

        if (messageFrame[0] == null){
            Helper.wait(messageFrame);
        }

        Assert.assertEquals(msg, messageFrame[0].getBodyAsString());
        Assert.assertEquals(customHeader1_value, messageFrame[0].getHeader(customHeader1_name));
        Assert.assertEquals(customHeader2_value, messageFrame[0].getHeader(customHeader2_name));

        client.getConnection().disconnect();
    }
}

