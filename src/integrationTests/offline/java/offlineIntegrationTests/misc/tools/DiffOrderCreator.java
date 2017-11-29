package offlineIntegrationTests.misc.tools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DiffOrderCreator {
    public static int sequenceCount;
    public static boolean isCancelled;

    public static String createDiffOrder() {
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
        if (!isCancelled) {
            order.put("a", "0.00039985");
        }
        if (!isCancelled) {
            order.put("v", "74.2822777");
        }
        order.put("s", isCancelled ? "cancelled" : "open");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(order);
        return jsonArray.toString();
    }
}
