package spring;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.messaging.tcp.ReconnectStrategy;
import org.springframework.messaging.tcp.TcpConnection;
import org.springframework.messaging.tcp.TcpConnectionHandler;
import org.springframework.messaging.tcp.TcpOperations;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpClient<P> implements TcpOperations<P>, TcpConnection<P> {

    private final int port;
    StompDecoder springStompDecoder = new StompDecoder();
    private Socket socket;
    private InputStream socketInputStream;
    private OutputStream socketOutputStream;
    private Incoming incoming;
    //private Outgoing outgoing;
    private String host;
    public TcpClient() throws IOException {
        this("localhost", 61613);
    }

    public TcpClient(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
    }

    private void handleExtractedByteMessages(List<Message<byte[]>> byteMessages) {
        for (Message<byte[]> message : byteMessages) {
            System.out.println("Received from the server:");
            System.out.println(message.toString());
        }
    }

    public ListenableFuture<Void> connect(final TcpConnectionHandler<P> connectionHandler) {

        System.out.println("CONNECTING");
        try {
            socket = new Socket(host, port);
            socketInputStream = socket.getInputStream();
            socketOutputStream = socket.getOutputStream();

            incoming = new Incoming();

            ExecutorService e = Executors.newFixedThreadPool(2);
            e.submit(incoming);
            e.shutdown();

            connectionHandler.afterConnected(this);
        } catch (IOException e) {
            connectionHandler.afterConnectFailure(e);
        }

        return null;
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
        return null;
    }

    /**
     * Shut down and close any open connections.
     *
     * @return a ListenableFuture that can be used to determine when and if the
     * connection is successfully closed
     */
    @Override
    public ListenableFuture<Void> shutdown() {
        return null;
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
            socketOutputStream.write(new StompEncoder().encode(message));
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

    }

    /**
     * Register a task to invoke after a period of write inactivity.
     *
     * @param runnable the task to invoke
     * @param duration the amount of inactive time in milliseconds
     */
    @Override
    public void onWriteInactivity(Runnable runnable, long duration) {

    }

    /**
     * Close the connection.
     */
    @Override
    public void close() {

    }

    private class Incoming implements Callable {

        @Override
        public Object call() throws IOException {

            System.out.println("LISTENING SERVER");

            while (true) {
                int initialBufferCapacity = (8 * 1024);
                java.nio.ByteBuffer readBuffer = ByteBuffer.allocate(initialBufferCapacity);

                final byte[] buffer = new byte[initialBufferCapacity];

                int read;
                while ((read = socketInputStream.read(buffer)) != -1) {
                    try {
                        readBuffer.limit(readBuffer.capacity());
                        if (readBuffer.remaining() < read) {
                            ByteBuffer biggerReadBuffer = ByteBuffer.allocate(readBuffer.capacity() + read);
                            biggerReadBuffer.put(readBuffer.array());
                            biggerReadBuffer.position(readBuffer.position());
                            readBuffer = biggerReadBuffer;
                        }
                        readBuffer.put(buffer, 0, read);
                        int lastPosition = readBuffer.position();
                        readBuffer.rewind();
                        readBuffer.limit(lastPosition);

                        List<Message<byte[]>> byteMessages = new LinkedList<>();
                        boolean extracted;
                        try {
                            extracted = byteMessages.addAll(springStompDecoder.decode(readBuffer));
                        } catch (Exception ex) {
                            throw new RuntimeException("Client STOMP messages decode error: " +
                                    "(" + ex.getClass().getSimpleName() + ") " + ex.getLocalizedMessage());
                        }

                        if (extracted) {
                            handleExtractedByteMessages(byteMessages);

                            ByteBuffer cutReadBuffer = ByteBuffer.allocate(readBuffer.capacity());
                            cutReadBuffer.put(readBuffer.array(), readBuffer.position(), readBuffer.remaining());
                            readBuffer = cutReadBuffer;
                        } else {
                            readBuffer.position(lastPosition);
                        }
                    } catch (RuntimeException ex) {
                        //m_connection.closeStompClientWithFatalError(ex.getLocalizedMessage());
                        break;
                    }
                }
                if (!false) {
                    if (!false) {
                        String msg = "Detected EOF on STOMP connection";
                    }
                }
            }
        }
    }
}
