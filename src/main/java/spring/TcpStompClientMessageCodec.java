package spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.BufferingStompDecoder;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.transport.SockJsSession;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

/**
 * Encode and decode STOMP TCP Socket messages.
 */
public class TcpStompClientMessageCodec {

    private static final Log logger = LogFactory.getLog(TcpStompClientMessageCodec.class);

    private static final StompEncoder ENCODER = new StompEncoder();

    private static final StompDecoder DECODER = new StompDecoder();

    private final BufferingStompDecoder bufferingDecoder;

    public TcpStompClientMessageCodec(int messageSizeLimit) {
        this.bufferingDecoder = new BufferingStompDecoder(DECODER, messageSizeLimit);
    }

    public List<Message<byte[]>> decode(WebSocketMessage<?> webSocketMessage) {
        List<Message<byte[]>> result = Collections.<Message<byte[]>>emptyList();
        ByteBuffer byteBuffer;
        if (webSocketMessage instanceof TextMessage) {
            byteBuffer = ByteBuffer.wrap(((TextMessage) webSocketMessage).asBytes());
        } else if (webSocketMessage instanceof BinaryMessage) {
            byteBuffer = ((BinaryMessage) webSocketMessage).getPayload();
        } else {
            return result;
        }
        result = this.bufferingDecoder.decode(byteBuffer);
        if (result.isEmpty()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Incomplete STOMP frame content received, bufferSize=" +
                        this.bufferingDecoder.getBufferSize() + ", bufferSizeLimit=" +
                        this.bufferingDecoder.getBufferSizeLimit() + ".");
            }
        }
        return result;
    }

    public WebSocketMessage<?> encode(Message<byte[]> message, Class<? extends WebSocketSession> sessionType) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.notNull(accessor, "No StompHeaderAccessor available");
        byte[] payload = message.getPayload();
        byte[] bytes = ENCODER.encode(accessor.getMessageHeaders(), payload);

        boolean useBinary = (payload.length > 0 &&
                !(SockJsSession.class.isAssignableFrom(sessionType)) &&
                MimeTypeUtils.APPLICATION_OCTET_STREAM.isCompatibleWith(accessor.getContentType()));

        return (useBinary ? new BinaryMessage(bytes) : new TextMessage(bytes));
    }
}
