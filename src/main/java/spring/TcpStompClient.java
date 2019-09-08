/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spring;

import org.springframework.context.Lifecycle;
import org.springframework.messaging.simp.stomp.BufferingStompDecoder;
import org.springframework.messaging.simp.stomp.ConnectionHandlingStompSession;
import org.springframework.messaging.simp.stomp.StompClientSupport;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.IOException;

/**
 * A STOMP over TCP client.
 *
 * @author Rostislav Dublin
 * @since 4.2
 */
public class TcpStompClient extends StompClientSupport implements Lifecycle {

    private final TcpClient tcpClient;
    private volatile boolean running = false;

    /**
     * Create an instance with host "127.0.0.1" and port 61613.
     */
    public TcpStompClient() throws IOException {
        this("127.0.0.1", 61613);
    }

    /**
     * Create an instance with the given host and port to connect to
     */
    public TcpStompClient(String host, int port) throws IOException {
        this.tcpClient = new TcpClient(host, port);
    }

    /**
     * Create an instance with a pre-configured TCP client.
     *
     * @param tcpClient the client to use
     */
    public TcpStompClient(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @Override
    public void start() {
        if (!isRunning()) {
            this.running = true;
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            this.running = false;
        }
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Connect and notify the given {@link StompSessionHandler} when connected
     * on the STOMP level.
     *
     * @param handler the handler for the STOMP session
     * @return ListenableFuture for access to the session when ready for use
     */
    public ListenableFuture<StompSession> connect(StompSessionHandler handler) {
        return connect(null, handler);
    }

    /**
     * An overloaded version of {@link #connect(StompSessionHandler)} that
     * accepts headers to use for the STOMP CONNECT frame.
     *
     * @param connectHeaders headers to add to the CONNECT frame
     * @param handler        the handler for the STOMP session
     * @return ListenableFuture for access to the session when ready for use
     */
    public ListenableFuture<StompSession> connect(StompHeaders connectHeaders, StompSessionHandler handler) {
        ConnectionHandlingStompSession session = createSession(connectHeaders, handler);
        this.tcpClient.connect(session);
        return session.getSessionFuture();
    }

    /**
     * Shut down the client and release resources.
     */
    public void shutdown() {
        this.tcpClient.shutdown();
    }

}
