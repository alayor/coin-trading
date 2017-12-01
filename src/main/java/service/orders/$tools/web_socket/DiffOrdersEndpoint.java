package service.orders.$tools.web_socket;

import org.json.JSONObject;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * It is used to send and receive messages to and from the Bitso Web Socket.
 */
public class DiffOrdersEndpoint extends Endpoint {
    private static Map<String, String> subscriptionInfo = new HashMap<>();
    private Session session;
    static {
        subscriptionInfo.put("action", "subscribe");
        subscriptionInfo.put("book", "btc_mxn");
        subscriptionInfo.put("type", "diff-orders");
    }

    private final DiffOrdersMessageHandler messageHandler;

    /**
     * Creates and returns a new instance using the specified message handler.
     * @param messageHandler to be used to process messages received from the Web Socket.
     */
    public DiffOrdersEndpoint(DiffOrdersMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * It sends a subscription message and assigns a message handler to process the received messages.
     * @param session used to assign the message handler.
     * @param config used to manipulated interaction with the Web Socket.
     */
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        session.addMessageHandler(messageHandler);
        sendMessage(new JSONObject(subscriptionInfo).toString());
    }

    private void sendMessage(String text) {
        try {
            session.getBasicRemote().sendText(text);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    boolean firstDiffOfferHasBeenReceived() {
        return messageHandler.firstDiffOfferHasBeenReceived();
    }
}
