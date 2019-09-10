package spring;

import io.netty.util.internal.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import other.Helper;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class StompClientSampleRun {
    public static String destination = null;
    static Log logger = LogFactory.getLog("StompClientSampleRun");

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        logger.debug("Started!");

        StompClient stompClient = new StompClient();
        stompClient.setMessageConverter(new StringMessageConverter());

        StompSessionHandler sessionHandler = new StompClientSessionHandler();

        StompSession session = stompClient.connect(sessionHandler).get();
        Helper.sleep(1000);

        System.out.println("1. SUBSCRIBE (optional)");
        System.out.println("1.1. Type destination to subscribe on. Enter to confirm subscription.");
        System.out.println("1.2. Simply press Enter to finish with subscriptions.");
        Scanner in = new Scanner(System.in);
        String msg;
        while (true) {
            msg = in.nextLine();
            if (StringUtil.isNullOrEmpty(msg)) {
                break;
            } else {
                session.subscribe(msg, sessionHandler);
            }
        }

        System.out.println("2. PUBLISH (optional)");
        System.out.println("2.1. Type destination to send to. Enter to confirm.");
        System.out.println("2.2. Simply press Enter to refuse publishing.");
        msg = in.nextLine();
        if (!StringUtil.isNullOrEmpty(msg)) {
            destination = msg;
            System.out.println("2.3 Type a message. Enter to PUBLISH ('EXIT'+Enter to stop): ");
        }
        System.out.println("3 Type 'STOP' and press Enter to stop the client)");
        while (true) {
            msg = in.nextLine();
            if (StringUtil.isNullOrEmpty(msg)) {
                continue;
            } else if (msg.equalsIgnoreCase("STOP")) {
                break;
            } else {
                if (destination != null) {
                    session.send(destination, msg);
                }
            }
        }

        in.close();
        session.disconnect();

    }
}
