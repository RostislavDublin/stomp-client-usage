package spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

public class StompClientSessionHandler extends StompSessionHandlerAdapter {

    private Log logger = LogFactory.getLog("StompClientSessionHandler");

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.debug("Received CONNECTED:\n- headers " + connectedHeaders.toString());
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        logger.debug("Received MESSAGE: " + payload.toString() + "\n- headers " + headers.toString());
    }

    /**
     * This implementation is empty.
     *
     * @param session
     * @param exception
     */
    @Override
    public void handleTransportError(StompSession session, Throwable exception) {

    }
}
