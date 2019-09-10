package spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.BufferingStompDecoder;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.messaging.simp.stomp.StompEncoder;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Encode and decode STOMP TCP Socket messages.
 */
public class StompMessageCodec {

    private static final Log logger = LogFactory.getLog(StompMessageCodec.class);

    private static final StompEncoder ENCODER = new StompEncoder();

    private static final StompDecoder DECODER = new StompDecoder();

    private final BufferingStompDecoder bufferingDecoder;

    public StompMessageCodec(int messageSizeLimit) {
        this.bufferingDecoder = new BufferingStompDecoder(DECODER, messageSizeLimit);
    }

    public List<Message<byte[]>> decode(ByteBuffer byteBuffer) {
        List<Message<byte[]>> result;

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

    public byte[] encode(Message<byte[]> message) {
        return ENCODER.encode(message);
    }
}
