package stomp;

import org.springframework.messaging.simp.stomp.ConnectionLostException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;

class StompSessionHandlerImpl implements StompSessionHandler {
    /**
     * Invoked when the session is ready to use, i.e. after the underlying
     * transport (TCP, WebSocket) is connected and a STOMP CONNECTED frame is
     * received from the broker.
     *
     * @param session          the client STOMP session
     * @param connectedHeaders the STOMP CONNECTED frame headers
     */
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("Connected");
    }

    /**
     * Handle any exception arising while processing a STOMP frame such as a
     * failure to convert the payload or an unhandled exception in the
     * application {@code StompFrameHandler}.
     *
     * @param session   the client STOMP session
     * @param command   the STOMP command of the frame
     * @param headers   the headers
     * @param payload   the raw payload
     * @param exception the exception
     */
    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        System.out.println("Exception");
    }

    /**
     * Handle a low level transport error which could be an I/O error or a
     * failure to encode or decode a STOMP message.
     * <p>Note that
     * {@link ConnectionLostException
     * ConnectionLostException} will be passed into this method when the
     * connection is lost rather than closed normally via
     * {@link StompSession#disconnect()}.
     *
     * @param session   the client STOMP session
     * @param exception the exception that occurred
     */
    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        System.out.println("Transport error");
    }

    /**
     * Invoked before {@link #handleFrame(StompHeaders, Object)} to determine the
     * type of Object the payload should be converted to.
     *
     * @param headers the headers of a message
     */
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return null;
    }

    /**
     * Handle a STOMP frame with the payload converted to the target type returned
     * from {@link #getPayloadType(StompHeaders)}.
     *
     * @param headers the headers of the frame
     * @param payload the payload or {@code null} if there was no payload
     */
    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.out.println("Handle frame");
    }
}
