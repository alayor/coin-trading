package offlineIntegrationTests.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ServerEndpoint(value = "/mock")
public class MockedServerEndpoint {
    private Session session;
    private ScheduledFuture<?> schedule;
    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    public MockedServerEndpoint() {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        schedule = scheduledThreadPoolExecutor.scheduleWithFixedDelay(
          this::sendDiffOrderMessage, 2, 2, TimeUnit.SECONDS);
    }

    @OnMessage
    public String onMessage(String message, Session session) throws JSONException {
        if (this.session == null) {
            this.session = session;
        }
        checkIfShouldStop(message);
        return "{\"response\":\"ok\", \"action\":\"subscribe\", \"type\": \"trades\"}";
    }

    private void checkIfShouldStop(String message) throws JSONException {
        JSONObject jsonObject = new JSONObject(message);
        if (schedule != null && "true".equals(jsonObject.optString("stop", ""))) {
            schedule.cancel(true);
            scheduledThreadPoolExecutor.shutdown();
        }
    }

    private void sendDiffOrderMessage() {
        if (this.session != null) {
            try {
                String diffOrder = createDiffOrder();
                this.session.getBasicRemote().sendText(diffOrder);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private String createDiffOrder() throws JSONException {
        Map<String, String> diffOrders = new HashMap<>();
        diffOrders.put("type", "diff-orders");
        diffOrders.put("book", "btc_mxn");
        diffOrders.put("payload", createOrder());
        diffOrders.put("sequence", "43760505");
        return new JSONObject(diffOrders).toString();
    }

    private String createOrder() throws JSONException {
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
