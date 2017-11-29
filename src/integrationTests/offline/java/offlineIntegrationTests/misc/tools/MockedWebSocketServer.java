package offlineIntegrationTests.misc.tools;

import org.json.JSONException;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/mock")
public class MockedWebSocketServer {
    private static Session session;

    public MockedWebSocketServer() {
    }

    @OnMessage
    public String onMessage(String message, Session session) throws JSONException {
        if (MockedWebSocketServer.session == null) {
            MockedWebSocketServer.session = session;
        }
        return "{\"response\":\"ok\", \"action\":\"subscribe\", \"type\": \"trades\"}";
    }

    public static void sendDiffOrderMessage() {
        if (session != null) {
            try {
                String diffOrder = DiffOrderCreator.createDiffOrder();
                session.getBasicRemote().sendText(diffOrder);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
