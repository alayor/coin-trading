package service.tools.web_socket;

import org.json.JSONObject;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static service.tools.AppProperties.getProperty;

public class WebSocketEndpoint extends Endpoint {
    private static Map<String, String> subscriptionInfo = new HashMap<>();

    static {
        subscriptionInfo.put("action", "subscribe");
        subscriptionInfo.put("book", getProperty("default_book"));
        subscriptionInfo.put("type", "trades");
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        session.addMessageHandler(new Handler());
        sendMessage(session, new JSONObject(subscriptionInfo).toString());
    }

    private void sendMessage(Session session, String text) {
        try {
            session.getBasicRemote().sendText(text);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private class Handler implements MessageHandler.Whole<String> {

        @Override
        public void onMessage(String message) {

        }
    }
}
