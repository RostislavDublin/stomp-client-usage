package spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.Lifecycle;
import org.springframework.messaging.Message;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class StompClientReader implements Runnable, Lifecycle {

    private static final Log logger = LogFactory.getLog(StompClientReader.class);
    ExecutorService e = Executors.newSingleThreadExecutor();
    private boolean running = false;
    private StompTcpOperations stompTcpOperations;

    public StompClientReader(StompTcpOperations stompTcpOperations) {
        this.stompTcpOperations = stompTcpOperations;
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        e.submit(this);
        e.shutdown();
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        logger.debug("Start LISTENING SERVER");
        running = true;
        try {
            while (running) {
                int initialBufferCapacity = (8 * 1024);
                final byte[] buffer = new byte[initialBufferCapacity];
                int read;
                while ((read = stompTcpOperations.getTcpConnection().getSocketInputStream().read(buffer)) != -1) {
                    List<Message<byte[]>> messages =
                            stompTcpOperations.getStompMessageCodec().decode(ByteBuffer.wrap(buffer, 0, read));
                    if (!messages.isEmpty()) {
                        for (Message<byte[]> message : messages) {
                            stompTcpOperations.getTcpConnection().setLastReadTime();
                            stompTcpOperations.getConnectionHandler().handleMessage((Message<byte[]>) message);
                        }
                    }
                }
                if (running) {
                    logger.warn("Detected EOF on STOMP connection");
                    break;
                }
            }
        } catch (Throwable t) {
            logger.error(t);
        } finally {
            running = false;
            logger.debug("Stop LISTENING SERVER");
            stompTcpOperations.shutdown();
        }
    }
}
