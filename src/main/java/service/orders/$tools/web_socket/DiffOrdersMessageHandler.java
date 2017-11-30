package service.orders.$tools.web_socket;

import org.json.JSONException;
import org.json.JSONObject;
import service.model.diff_orders.DiffOrderResult;
import service.orders.$tools.holders.CurrentDiffOrdersHolder;

import javax.websocket.MessageHandler;
import java.util.concurrent.TimeUnit;

public class DiffOrdersMessageHandler implements MessageHandler.Whole<String> {
    private static DiffOrdersMessageHandler diffOrdersMessageHandler;
    private CurrentDiffOrdersHolder ordersHolder;
    private volatile boolean wasSuccessfullySubscribed;
    private volatile boolean firstDiffOfferHasBeenReceived;

    private DiffOrdersMessageHandler(CurrentDiffOrdersHolder orderHolder) {
        ordersHolder = orderHolder;
    }

    public static DiffOrdersMessageHandler getInstance(CurrentDiffOrdersHolder orderHolder) {
        if (diffOrdersMessageHandler == null) {
            diffOrdersMessageHandler = new DiffOrdersMessageHandler(orderHolder);
        }
        return diffOrdersMessageHandler;
    }

    public static DiffOrdersMessageHandler getInstance() {
        return getInstance(CurrentDiffOrdersHolder.getInstance());
    }

    @Override
    public void onMessage(String message) {
        try {
            processMessage(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void processMessage(String message) throws JSONException, InterruptedException {
        JSONObject jsonObject = new JSONObject(message);
        if (isSubscriptionMessage(jsonObject)) {
            checkSubscriptionWasSuccessful(jsonObject);
        }
        if (isDiffOrderMessage(jsonObject)) {
            firstDiffOfferHasBeenReceived = true;
            addOrderResult(jsonObject);
        }
    }

    private void addOrderResult(JSONObject jsonObject) throws InterruptedException {
        DiffOrderResult diffOrderResult = DiffOrderResult.parse(jsonObject);
        ordersHolder.produce(diffOrderResult);
    }

    private boolean isDiffOrderMessage(JSONObject jsonObject) throws JSONException {
        return jsonObject.has("payload") && jsonObject.has("type") &&
          "diff-orders".equals(jsonObject.getString("type"));
    }

    private void checkSubscriptionWasSuccessful(JSONObject jsonObject) throws JSONException {
        if ("ok".equals(jsonObject.getString("response"))) {
            wasSuccessfullySubscribed = true;
        } else {
            throw new RuntimeException("Error connecting to bitso web socket.");
        }
    }

    private boolean isSubscriptionMessage(JSONObject jsonObject) throws JSONException {
        return jsonObject.has("action") &&
          "subscribe".equals(jsonObject.getString("action"));
    }

    public boolean wasSuccessfullySubscribed() {
        return wasSuccessfullySubscribed;
    }


    public DiffOrderResult getNext(int timeout, TimeUnit seconds) throws InterruptedException {
        return ordersHolder.getNext(timeout, seconds);
    }

    public void clearDiffOrders() throws InterruptedException {
        ordersHolder.clear();
    }

    public boolean firstDiffOfferHasBeenReceived() {
        return firstDiffOfferHasBeenReceived;
    }

    public static void clearInstance() {
        diffOrdersMessageHandler = null;
    }
}
