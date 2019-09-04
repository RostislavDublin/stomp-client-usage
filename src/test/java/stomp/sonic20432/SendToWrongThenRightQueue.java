package stomp.sonic20432;

import other.Helper;
import other.Say;
import stomp.VertxStompClient;

public class SendToWrongThenRightQueue {
    private static Say say = new Say();

    public static void main(String[] args) {
        VertxStompClient client = new VertxStompClient("10.211.55.3", 61613, "D01", "D01");
        client.connect();

        //client.send("/queue/BadQ", "Message to the bad queue");

        //Helper.sleep(5000);
        //client.send("/queue/SampleQ11", "Message to the good queue");

        client.subscribe("/queue/SampleQ1", "sq1", "auto");
        client.subscribe("/queue/SampleQ2", "sq2", "auto");
        client.subscribe("/queue/SampleQ3", "sq3", "auto");

        client.subscribe("/topic/T1", "st1", "auto");
        client.subscribe("/topic/T2", "st2", "auto");
        client.subscribe("/topic/T3", "st3", "auto");

        client.send("/queue/SampleQ1", "Message to the queue SampleQ1");
        client.send("/queue/SampleQ2", "Message to the queue SampleQ2");
        client.send("/queue/SampleQ3", "Message to the queue SampleQ3");

        Helper.sleep(5000);

        client.send("/topic/T1", "Message to the topic T1");
        client.send("/topic/T2", "Message to the topic T2");
        client.send("/topic/T3", "Message to the topic T3");

        Helper.sleep(5000);

        client.close();
    }
}

