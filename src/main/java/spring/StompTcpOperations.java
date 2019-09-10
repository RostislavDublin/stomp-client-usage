package spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.Lifecycle;
import org.springframework.messaging.tcp.ReconnectStrategy;
import org.springframework.messaging.tcp.TcpConnectionHandler;
import org.springframework.messaging.tcp.TcpOperations;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;

public class StompTcpOperations<P> implements TcpOperations<P>, Lifecycle {

    private final int port;
    private StompTcpConnection<P> tcpConnection;
    private SettableListenableFuture<Void> tcpConnectionFuture;
    private Log logger = LogFactory.getLog("StompTcpOperations");
    private StompClientReader receiver;
    private String host;
    private TcpConnectionHandler<P> connectionHandler;
    private ThreadPoolTaskScheduler taskScheduler; // for heartbeats
    private boolean running = false;
    private StompMessageCodec stompMessageCodec = new StompMessageCodec(Integer.MAX_VALUE);


    public StompTcpOperations(String host, int port) throws IOException {
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();

        receiver = new StompClientReader(this);
        this.host = host;
        this.port = port;
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public TcpConnectionHandler<P> getConnectionHandler() {
        return connectionHandler;
    }

    public ListenableFuture<Void> connect(final TcpConnectionHandler<P> connectionHandler) {
        this.connectionHandler = connectionHandler;
        start();
        return tcpConnectionFuture;
    }

    /**
     * Open a new connection and a strategy for reconnecting if the connection fails.
     *
     * @param connectionHandler a handler to manage the connection
     * @param reconnectStrategy a strategy for reconnecting
     * @return a ListenableFuture that can be used to determine when and if the
     * initial connection is successfully established
     */
    @Override
    public ListenableFuture<Void> connect(TcpConnectionHandler<P> connectionHandler,
                                          ReconnectStrategy reconnectStrategy) {
        throw new NotImplementedException();
    }

    /**
     * Shut down and close any open connections.
     *
     * @return a ListenableFuture that can be used to determine when and if the
     * connection is successfully closed
     */
    @Override
    public ListenableFuture<Void> shutdown() {
        SettableListenableFuture<Void> future = new SettableListenableFuture<>();
        stop();
        return future;
    }

    public StompTcpConnection<P> getTcpConnection() {
        return tcpConnection;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void start() {
        if (!isRunning()) {
            running = true;
            logger.debug("Start CONNECTING");
            tcpConnectionFuture = new SettableListenableFuture<>();
            try {
                tcpConnection = new StompTcpConnection<P>(this);
                receiver.start();
                connectionHandler.afterConnected(tcpConnection);
            } catch (IOException e) {
                connectionHandler.afterConnectFailure(e);
                tcpConnectionFuture.setException(e);
                logger.error("Error CONNECTING", e);
            }
        }
    }

    @Override
    public void stop() {
        if (!isRunning()) {
            running = false;
            receiver.stop();
            tcpConnection.close();
            taskScheduler.shutdown();
            connectionHandler.afterConnectionClosed();
        }
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    public StompMessageCodec getStompMessageCodec() {
        return stompMessageCodec;
    }
}
