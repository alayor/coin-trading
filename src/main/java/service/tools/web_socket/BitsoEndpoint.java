package service.tools.web_socket;

import org.json.JSONObject;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static service.tools.AppProperties.getProperty;

public class BitsoEndpoint extends Endpoint {
    private static Map<String, String> subscriptionInfo = new HashMap<>();
    private Session session;
    static {
        subscriptionInfo.put("action", "subscribe");
        subscriptionInfo.put("book", getProperty("default_book"));
        subscriptionInfo.put("type", "trades");
    }

    private final BitsoMessageHandler messageHandler;

    public BitsoEndpoint(BitsoMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        session.addMessageHandler(messageHandler);
        sendMessage(new JSONObject(subscriptionInfo).toString());
    }

    public void sendMessage(String text) {
        try {
            session.getBasicRemote().sendText(text);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
