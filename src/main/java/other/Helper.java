package other;

import io.vertx.ext.stomp.Frame;

public class Helper {

    public static void sleep(int howLongToSleep) {
        System.out.println(" . . . sleep . . .");
        try {
            Thread.sleep(howLongToSleep);
        } catch (InterruptedException e) {
            ///e.printStackTrace();
        }
    }

    public static void wait(Object o) {
        try {
            synchronized (o) {
                o.wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void notifyAll(Frame[] messageFrame) {
        synchronized (messageFrame) {
            messageFrame.notifyAll();
        }
    }
}
