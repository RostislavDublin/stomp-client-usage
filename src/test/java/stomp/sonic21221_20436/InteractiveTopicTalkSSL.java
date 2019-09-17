package stomp.sonic21221_20436;

import io.netty.util.internal.StringUtil;
import io.vertx.core.net.JksOptions;
import other.Say;
import stomp.VertxStompClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This is interactive Queue Sender/Receiver application. (Use Sonic SMC TestClient as a counterpart).
 * It subscribes and listens the 'SampleQ1' queue. Incoming messages output on the console.
 * It also can send messages to the 'SampleQ2' queue. Type a message on console and press Enter to send.
 * Every outgoing message goes with two custom headers.
 * <p>
 * Before the test, set the following at Broker:
 * - advanced property:
 * -- name: DEBUG_PARAMETERS.DEBUG_NAME
 * -- value: StompSender:65536;StompListener:65536;StompAgentListener:65536;StompAgentSender:65536;
 * StompSubscriptionHandler:65536
 * to see STOMP/JMS messages exchange through the Broker
 */
public class InteractiveTopicTalkSSL {
    private static Say say = new Say();
    private static String subscriptionId = "stomp-subscription-1";
    private static String sendTo = "/topic/t1";
    private static String receiveFrom = "/topic/t2";

    private static String customHeader1_name = "custom-header-1";
    private static String customHeader1_value = "custom-value-1";
    private static String customHeader2_name = "custom-header-2";
    private static String customHeader2_value = "custom-value-2";

    public static void main(String[] args) {
        VertxStompClient client = new VertxStompClient("10.211.55.3", 61614, "D01", "D01");
        client.getOptions()
              .setSsl(true)
              .setTrustStoreOptions(
                      new JksOptions().setPath("trustStore.jks").setPassword("password")
              );

        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put(customHeader1_name, customHeader1_value);
        customHeaders.put(customHeader2_name, customHeader2_value);

        client.connect();

        client.subscribeAutoAck(receiveFrom, subscriptionId, event -> {
            System.out.println("< " + event.getCommand().name() + " on SUBSCRIPTION [" + subscriptionId + "]: "
                    + " hh: " + event.getHeaders().toString()
                    + " pl: " + event.getBodyAsString());
        });

        System.out.println("Enter a message. Enter to send ('EXIT'+Enter to stop): ");
        Scanner in = new Scanner(System.in);
        while (true) {
            String msg = in.nextLine();
            if (StringUtil.isNullOrEmpty(msg)) {
                continue;
            } else if (msg.equalsIgnoreCase("EXIT")) {
                break;
            } else {
                client.send(sendTo, msg, new HashMap<>(customHeaders));
            }
        }
        in.close();
        client.close();
    }
}

