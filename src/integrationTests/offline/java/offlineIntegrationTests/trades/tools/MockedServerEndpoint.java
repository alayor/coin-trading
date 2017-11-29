package offlineIntegrationTests.trades.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint(value = "/mock")
public class MockedServerEndpoint {
    private static Session session;
    public static int sequenceCount;

    public MockedServerEndpoint() {
    }

    @OnMessage
    public String onMessage(String message, Session session) throws JSONException {
        if (MockedServerEndpoint.session == null) {
            MockedServerEndpoint.session = session;
        }
        return "{\"response\":\"ok\", \"action\":\"subscribe\", \"type\": \"trades\"}";
    }

    public static void sendDiffOrderMessage() {
        if (session != null) {
            try {
                String diffOrder = createDiffOrder();
                session.getBasicRemote().sendText(diffOrder);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private static String createDiffOrder() {
        Map<String, String> diffOrders = new HashMap<>();
        diffOrders.put("type", "diff-orders");
        diffOrders.put("book", "btc_mxn");
        diffOrders.put("payload", createOrder());
        diffOrders.put("sequence", String.valueOf(++sequenceCount));
        return new JSONObject(diffOrders).toString();
    }

    private static String createOrder() {
        Map<String, String> order = new HashMap<>();
        order.put("o", "4cCTdGxIo8iyhH5Z");
        order.put("d", "1511918888029");
        order.put("r", "185775.36");
        order.put("t", "1");
        order.put("a", "0.00039985");
        order.put("v", "74.2822777");
        order.put("s", "open");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(order);
        return jsonArray.toString();
    }
}
