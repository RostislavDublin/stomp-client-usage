package spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.tcp.TcpConnection;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class StompTcpConnection<P> implements TcpConnection<P> {
    private final StompTcpOperations stompTcpOperations;
    private final List<ScheduledFuture<?>> inactivityTasks = new ArrayList<ScheduledFuture<?>>(2);
    private Socket socket;
    private InputStream socketInputStream;
    private OutputStream socketOutputStream;
    private Log logger = LogFactory.getLog("StompTcpConnection");
    private volatile long lastReadTime = -1;
    private volatile long lastWriteTime = -1;

    public StompTcpConnection(StompTcpOperations stompTcpOperations) throws IOException {
        this.stompTcpOperations = stompTcpOperations;

        socket = new Socket(stompTcpOperations.getHost(), stompTcpOperations.getPort());
        socketInputStream = socket.getInputStream();
        socketOutputStream = socket.getOutputStream();
    }

    public void setLastReadTime() {
        this.lastReadTime = System.currentTimeMillis();
    }

    public InputStream getSocketInputStream() {
        return socketInputStream;
    }

    public OutputStream getSocketOutputStream() {
        return socketOutputStream;
    }

    /**
     * Send the given message.
     *
     * @param message the message
     * @return a ListenableFuture that can be used to determine when and if the
     * message was successfully sent
     */
    @Override
    public ListenableFuture<Void> send(Message message) {
        SettableListenableFuture<Void> future = new SettableListenableFuture<>();
        try {
            socketOutputStream.write(stompTcpOperations.getStompMessageCodec().encode(message));
            future.set(null);
        } catch (Throwable ex) {
            future.setException(ex);
        }
        return future;
    }

    /**
     * Register a task to invoke after a period of read inactivity.
     *
     * @param runnable the task to invoke
     * @param duration the amount of inactive time in milliseconds
     */
    @Override
    public void onReadInactivity(Runnable runnable, long duration) {
        logger.debug(String.format("Charging ReadInactivity task (on %s seconds inactivity).", duration));
        Assert.state(stompTcpOperations.getTaskScheduler() != null, "No TaskScheduler configured");
        this.lastReadTime = System.currentTimeMillis();
        this.inactivityTasks.add(stompTcpOperations.getTaskScheduler().scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - lastReadTime > duration) {
                    try {
                        runnable.run();
                    } catch (Throwable ex) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("ReadInactivityTask failure", ex);
                        }
                    }
                }
            }
        }, duration / 2));

    }

    /**
     * Register a task to invoke after a period of write inactivity.
     *
     * @param runnable the task to invoke
     * @param duration the amount of inactive time in milliseconds
     */
    @Override
    public void onWriteInactivity(final Runnable runnable, final long duration) {
        logger.debug(String.format("Charging WriteInactivity task (on %s seconds inactivity).", duration));
        Assert.state(stompTcpOperations.getTaskScheduler() != null, "No TaskScheduler configured");
        this.lastWriteTime = System.currentTimeMillis();
        this.inactivityTasks.add(stompTcpOperations.getTaskScheduler().scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - lastWriteTime > duration) {
                    try {
                        runnable.run();
                    } catch (Throwable ex) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("WriteInactivityTask failure", ex);
                        }
                    }
                }
            }
        }, duration / 2));
    }

    /**
     * Close the connection.
     */
    @Override
    public void close() {
        try {
            cancelInactivityTasks();
            socket.close();
        } catch (IOException e) {
            logger.warn(String.format("Closing socket", e));
        }
    }

    private void cancelInactivityTasks() {
        for (ScheduledFuture<?> task : this.inactivityTasks) {
            try {
                task.cancel(true);
            } catch (Throwable ex) {
                logger.debug("Inactivity task cancel failure", ex);
            }
        }
        this.lastReadTime = -1;
        this.lastWriteTime = -1;
        this.inactivityTasks.clear();
    }

}
